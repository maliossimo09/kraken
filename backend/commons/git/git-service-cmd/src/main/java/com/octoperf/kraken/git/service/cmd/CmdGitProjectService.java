package com.octoperf.kraken.git.service.cmd;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.octoperf.kraken.command.entity.Command;
import com.octoperf.kraken.command.executor.api.CommandService;
import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Mono;

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

  @Override
  public Mono<GitConfiguration> connect(final Owner owner, final String repositoryUrl) {
    final var rootPath = ownerToPath.apply(owner);

    return Mono.fromCallable(() -> Files.createTempDirectory(owner.getUserId()))
        .flatMap(tmp -> commandService.validate(Command.builder()
            .path(rootPath.toString())
            .environment(ImmutableMap.of())
            .args(ImmutableList.of("git", "clone", repositoryUrl, tmp.toString()))
            .build())
            .flatMapMany(commandService::execute)
            .collectList()
            .map(strings -> tmp))
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
