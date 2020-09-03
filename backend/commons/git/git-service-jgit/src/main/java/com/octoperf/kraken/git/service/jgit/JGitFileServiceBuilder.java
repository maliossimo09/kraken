package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.git.entity.command.GitCommand;
import com.octoperf.kraken.git.service.api.GitFileService;
import com.octoperf.kraken.git.service.api.GitFileServiceBuilder;
import com.octoperf.kraken.git.service.jgit.command.GitCommandExecutor;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.tools.event.bus.EventBus;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitFileServiceBuilder implements GitFileServiceBuilder {

  @NonNull OwnerToTransportConfig ownerToTransportConfig;
  @NonNull OwnerToPath ownerToPath;
  @NonNull EventBus eventBus;
  @NonNull List<GitCommandExecutor<GitCommand>> commandExecutors;

  @Override
  public Mono<GitFileService> build(final Owner owner) {
    return ownerToTransportConfig.apply(owner).flatMap(transportConfigCallback -> Mono.fromCallable(() -> {
      final var root = this.ownerToPath.apply(owner);
      final var git = Git.open(root.toFile());
      final var map = commandExecutors.stream().collect(Collectors.toMap(GitCommandExecutor::getCommandClass, executor -> executor));
      return new JGitFileService(owner, root, git, transportConfigCallback, eventBus, map);
    }));
  }
}
