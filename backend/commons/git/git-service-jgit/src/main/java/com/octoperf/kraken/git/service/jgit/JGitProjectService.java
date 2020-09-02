package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitProjectService implements GitProjectService {

  private static final Path DOT_GIT_PATH = Paths.get(".git");

  @NonNull OwnerToTransportConfig ownerToTransportConfig;
  @NonNull OwnerToPath ownerToPath;

  @Override
  public Mono<GitConfiguration> connect(final Owner owner, final String repositoryUrl) {
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

    return cloneRepo.then(this.getConfiguration(owner));
  }

  @Override
  public Mono<GitConfiguration> getConfiguration(Owner owner) {
    final var rootPath = ownerToPath.apply(owner);
    return Mono.fromCallable(() -> {
      final var repositoryBuilder = new FileRepositoryBuilder();
      repositoryBuilder.setMustExist(true);
      repositoryBuilder.setGitDir(rootPath.resolve(".git").toFile());
      final var repository = repositoryBuilder.build();
      return GitConfiguration.builder().repositoryUrl(repository.getConfig().getString("remote", "origin", "url")).build();
    });
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
