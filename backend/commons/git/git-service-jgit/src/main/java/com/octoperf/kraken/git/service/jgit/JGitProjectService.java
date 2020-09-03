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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitProjectService implements GitProjectService {

  private static final Path DOT_GIT_PATH = Paths.get(".git");

  @NonNull OwnerToTransportConfig ownerToTransportConfig;
  @NonNull OwnerToPath ownerToPath;
  @NonNull Supplier<CloneCommand> commandSupplier;
  @NonNull Supplier<FileRepositoryBuilder> repositoryBuilderSupplier;

  @Override
  public Mono<GitConfiguration> connect(final Owner owner, final String repositoryUrl) {
    final var cloneRepo =
        this.ownerToTransportConfig.apply(owner).flatMap(transportConfigCallback -> Mono.fromCallable(() -> {
          // Clone into a temporary folder
          final var tmp = Files.createTempDirectory(owner.getUserId());
          final var command = commandSupplier.get();;
          command.setURI(repositoryUrl)
              .setDirectory(tmp.toFile())
              .setTransportConfigCallback(transportConfigCallback);
          command.call();
          return tmp;
        }));

    return cloneRepo
        .flatMap(tmp -> Mono.fromCallable(() -> {
          // Copy the cloned repo to the application and remove the tmp folder
          final var rootPath = ownerToPath.apply(owner);
          FileSystemUtils.copyRecursively(tmp, rootPath);
          FileSystemUtils.deleteRecursively(tmp);
          return null;
        }))
        .then(this.getConfiguration(owner));
  }

  @Override
  public Mono<GitConfiguration> getConfiguration(final Owner owner) {
    final var rootPath = ownerToPath.apply(owner);
    return Mono.fromCallable(() -> {
      final var repositoryBuilder = this.repositoryBuilderSupplier.get(); //new FileRepositoryBuilder();
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
