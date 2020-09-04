package com.octoperf.kraken.git.service.jgit;

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

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
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
  @NonNull List<GitCommandExecutor> commandExecutors;
  @NonNull Function<Path, Mono<Git>> gitFactory;

  @Override
  public Mono<GitFileService> build(final Owner owner) {
    final var root = this.ownerToPath.apply(owner);
    final var map = commandExecutors.stream().collect(Collectors.toMap(GitCommandExecutor::getCommandClass, executor -> executor));
    return Mono.zip(gitFactory.apply(root), ownerToTransportConfig.apply(owner))
        .map(t2 -> new JGitFileService(owner, t2.getT1(), t2.getT2(), eventBus, map));
  }
}
