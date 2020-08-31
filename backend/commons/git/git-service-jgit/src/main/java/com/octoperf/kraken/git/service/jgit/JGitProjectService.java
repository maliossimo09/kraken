package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.project.entity.Project;
import com.octoperf.kraken.security.authentication.client.api.AuthenticatedClientBuildOrder;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.storage.client.api.StorageClientBuilder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitProjectService implements GitProjectService {

  private static final String GIT_CONFIGURATION_PATH = Paths.get(".kraken", "git.json").toString();

  @NonNull StorageClientBuilder storageClientBuilder;
  @NonNull OwnerToTransportConfig ownerToTransportConfig;

  @Override
  public Mono<GitConfiguration> connect(final Owner owner, final String repositoryUrl) {
    final var config = GitConfiguration.builder().repositoryUrl(repositoryUrl).build();
    final var transportConfig = this.ownerToTransportConfig.apply(owner);

    // TODO OwnerToPath publique et l'utiliser ici et dans le GitCredentialsService
    final CloneCommand command = new CloneCommand();
    command.setURI("git@github.com:geraldpereira/gatlingTest.git")
        .setDirectory(new File("testDir/gatling"))
        .setTransportConfigCallback(transportConfigCallback);
    command.call();

    return storageClientBuilder.build(AuthenticatedClientBuildOrder.builder().session(owner).build())
        .flatMap(storageClient -> storageClient.setJsonContent(GIT_CONFIGURATION_PATH, config))
        .map(storageNode -> config);
    // TODO fetch data

  }

  @Override
  public Mono<Void> disconnect(final Owner owner, final GitConfiguration git) {
    // Remove the git configuration

    // Remove the .git folder

    return null;
  }

  @Override
  public Mono<Project> importFromRepository(final Owner owner, final String repositoryUrl) {
    // ???

    return null;
  }
}
