package com.octoperf.kraken.git.service.cmd;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.octoperf.kraken.command.entity.Command;
import com.octoperf.kraken.command.executor.api.CommandService;
import com.octoperf.kraken.config.api.ApplicationProperties;
import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class CmdGitProjectService implements GitProjectService {

  private static final Path DOT_GIT_PATH = Paths.get(".git");

  @NonNull OwnerToPath ownerToPath;
  @NonNull CommandService commandService;
  @NonNull ApplicationProperties properties;
  @NonNull UserIdToCommandEnvironment toCommandEnvironment;
  @NonNull UserProvider userProvider;

  @Override
  public Mono<GitConfiguration> connect(final Owner owner, final String repositoryUrl) {
    final var rootPath = ownerToPath.apply(owner);
    return userProvider.getAuthenticatedUser()
        .flatMap(user -> Mono.fromCallable(() -> Files.createTempDirectory(Paths.get(properties.getData()), "git-tmp-" + owner.getUserId())).map(path -> Tuples.of(user, path)))
        .flatMap(t2 -> {
              final var tmp = t2.getT2();
              final var email = t2.getT1().getEmail();
              final var env = toCommandEnvironment.apply(owner.getUserId());
              final var commands = ImmutableList.of(
                  // Init repo
                  Command.builder().path(tmp.toString()).environment(ImmutableMap.of()).args(ImmutableList.of("git", "init")).build(),
                  // Download symlinks as simple text files
                  Command.builder().path(tmp.toString()).environment(ImmutableMap.of()).args(ImmutableList.of("git", "config", "core.symlinks", "false")).build(),
                  // Config name / email
                  Command.builder().path(tmp.toString()).environment(ImmutableMap.of()).args(ImmutableList.of("git", "config", "user.email", email)).build(),
                  Command.builder().path(tmp.toString()).environment(ImmutableMap.of()).args(ImmutableList.of("git", "config", "author.email", email)).build(),
                  Command.builder().path(tmp.toString()).environment(ImmutableMap.of()).args(ImmutableList.of("git", "config", "committer.email", email)).build(),
                  // Set remote and pull repository
                  Command.builder().path(tmp.toString()).environment(ImmutableMap.of()).args(ImmutableList.of("git", "remote", "add", "origin", repositoryUrl)).build(),
                  Command.builder().path(tmp.toString()).environment(env).args(ImmutableList.of("git", "pull", "origin", "master")).build(),
                  Command.builder().path(tmp.toString()).environment(ImmutableMap.of()).args(ImmutableList.of("git", "branch", "--set-upstream-to=origin/master", "master")).build()
              );
              return commandService.validate(commands)
                  .flatMapMany(commandService::execute)
                  .collectList()
                  .map(logs -> {
                    logs.forEach(log::info);
                    return tmp;
                  });
            }
        )
        .flatMap(tmp -> Mono.fromCallable(() -> {
          // Copy the cloned repo to the application and remove the tmp folder
          FileSystemUtils.copyRecursively(tmp, rootPath);
          FileSystemUtils.deleteRecursively(tmp);
          return null;
        }))
        .then(this.getConfiguration(owner));
  }

  @Override
  public Mono<GitConfiguration> getConfiguration(final Owner owner) {
    final var rootPath = ownerToPath.apply(owner);
    return commandService.validate(Command.builder()
        .path(rootPath.toString())
        .environment(ImmutableMap.of())
        .args(ImmutableList.of("git", "config", "--get", "remote.origin.url"))
        .build())
        .flatMapMany(commandService::execute)
        .next()
        .map(url -> GitConfiguration.builder().repositoryUrl(url).build());
  }

  @Override
  public Mono<Void> disconnect(final Owner owner) {
    final var rootPath = ownerToPath.apply(owner);
    return Mono.fromCallable(() -> {
      FileSystemUtils.deleteRecursively(rootPath.resolve(DOT_GIT_PATH));
      return null;
    });
  }
}
