package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.git.service.api.GitFileService;
import com.octoperf.kraken.git.service.api.GitFileServiceBuilder;
import com.octoperf.kraken.security.authentication.client.api.AuthenticatedClientBuildOrder;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.storage.client.api.StorageClientBuilder;
import com.octoperf.kraken.tools.event.bus.EventBus;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitFileServiceBuilder implements GitFileServiceBuilder {

  @NonNull OwnerToTransportConfig ownerToTransportConfig;
  @NonNull OwnerToPath ownerToPath;
  @NonNull EventBus eventBus;
  @NonNull StorageClientBuilder storageClientBuilder;

  @Override
  public Mono<GitFileService> build(final Owner owner) {
    return Mono.zip(storageClientBuilder.build(AuthenticatedClientBuildOrder.builder().session(owner).build()), ownerToTransportConfig.apply(owner)).flatMap(t2 -> Mono.fromCallable(() -> {
      final var root = this.ownerToPath.apply(owner);
      final var git = Git.open(root.toFile());
      return new JGitFileService(owner, root, git, t2.getT2(), t2.getT1(), eventBus);
    }));
  }
}
