package com.octoperf.kraken.git.service.jgit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.git.service.api.GitUserService;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitProjectService implements GitProjectService {

  private static final Path GIT_CONFIGURATION_PATH = Paths.get("git.json");
  private static final Path DOT_GIT_PATH = Paths.get(".git");

  @NonNull ObjectMapper mapper;
  @NonNull OwnerToTransportConfig ownerToTransportConfig;
  @NonNull OwnerToPath ownerToPath;
  @NonNull GitUserService userService;

  @Override
  public Mono<GitConfiguration> connect(final Owner owner, final String repositoryUrl) {
    final var writeConf = Mono.fromCallable(() -> {
      // Write the git.json file as a sibling of the application folder
      final var rootPath = ownerToPath.apply(owner.toBuilder().applicationId("").build());
      final var config = GitConfiguration.builder().repositoryUrl(repositoryUrl).build();
      Files.writeString(rootPath.resolve(GIT_CONFIGURATION_PATH), mapper.writeValueAsString(config));
      return config;
    });

    final var cloneRepo =
        this.ownerToTransportConfig.apply(owner).flatMap(transportConfigCallback -> Mono.fromCallable(() -> {
          final var rootPath = ownerToPath.apply(owner);
          final CloneCommand command = new CloneCommand();
          command.setURI(repositoryUrl)
              .setDirectory(rootPath.toFile())
              .setTransportConfigCallback(transportConfigCallback);
          command.call();
          return null;
        }));

    return cloneRepo.then(writeConf);
  }

  @Override
  public Mono<GitConfiguration> getConfiguration(Owner owner) {
    final var rootPath = ownerToPath.apply(owner);
    return Mono.fromCallable(() -> {
      final var str = Files.readString(rootPath.resolve(GIT_CONFIGURATION_PATH), UTF_8);
      return mapper.readValue(str, GitConfiguration.class);
    });
  }

  @Override
  public Mono<Void> disconnect(final Owner owner, final GitConfiguration git) {
    final var rootPath = ownerToPath.apply(owner);

    final var deleteDotSSH = this.userService.removeCredentials(owner.getUserId());

    final var deleteGitConfiguration = Mono.fromCallable(() -> {
      Files.delete(rootPath.resolve(GIT_CONFIGURATION_PATH));
      return null;
    });

    final var deleteDotGit = Mono.fromCallable(() -> {
      FileSystemUtils.deleteRecursively(rootPath.resolve(DOT_GIT_PATH));
      return null;
    });

    return Mono.zip(deleteDotGit, deleteGitConfiguration, deleteDotSSH).then();
  }
}
