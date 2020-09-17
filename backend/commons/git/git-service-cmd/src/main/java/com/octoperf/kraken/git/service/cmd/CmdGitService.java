package com.octoperf.kraken.git.service.cmd;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.octoperf.kraken.command.entity.Command;
import com.octoperf.kraken.command.executor.api.CommandService;
import com.octoperf.kraken.git.entity.GitStatus;
import com.octoperf.kraken.git.event.GitRefreshStorageEvent;
import com.octoperf.kraken.git.event.GitStatusUpdateEvent;
import com.octoperf.kraken.git.service.api.GitLogsService;
import com.octoperf.kraken.git.service.api.GitService;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.tools.event.bus.EventBus;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.octoperf.kraken.security.entity.owner.OwnerType.USER;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class CmdGitService implements GitService {

  private static final int MAX_EVENTS_SIZE = 100;
  private static final Duration MAX_EVENTS_TIMEOUT_MS = Duration.ofMillis(5000);

  @NonNull OwnerToPath ownerToPath;
  @NonNull CommandService commandService;
  @NonNull GitLogsService logsService;
  @NonNull EventBus eventBus;

  @Override
  public Mono<Void> execute(final Owner owner, final String command) {
    return commandService.parseCommandLine(command)
        .flatMap(args -> Mono.fromCallable(() -> {
          checkArgument(args.size() >= 2);
          checkArgument("git".equals(args.get(0)), "Only git commands are supported.");
          final var subCommand = GitSubCommand.valueOf(args.get(1));
          final var cmd = Command.builder()
              .args(args)
              .path(ownerToPath.apply(owner).toString())
              .environment(ImmutableMap.of())
              .build();
          final var logsFlux = commandService.validate(cmd)
              .flatMapMany(commandService::execute)
              .doOnComplete(() -> {
                if (subCommand.isRefresh()) {
                  eventBus.publish(GitRefreshStorageEvent.builder().owner(owner).build());
                }
              });
          logsService.push(owner, logsFlux);
          return null;
        }));
  }

  @Override
  public Mono<GitStatus> status(final Owner owner) {
    // TODO parse status
    return null;
  }

  @Override
  public Flux<GitStatus> watchStatus(final Owner owner) {
    return this.eventBus.of(GitStatusUpdateEvent.class)
        .filter(event -> event.getOwner().equals(owner))
        .windowTimeout(MAX_EVENTS_SIZE, MAX_EVENTS_TIMEOUT_MS)
        .flatMap(window -> this.status(owner));
  }

  @Override
  public Flux<GitRefreshStorageEvent> watchRefresh(final Owner owner) {
    return this.eventBus.of(GitRefreshStorageEvent.class)
        .filter(event -> event.getOwner().equals(owner))
        .windowTimeout(MAX_EVENTS_SIZE, MAX_EVENTS_TIMEOUT_MS)
        .flatMap(window -> window.reduce((event1, event2) -> event2));
  }

  private Path userIdToPath(final String userId) {
    return this.ownerToPath.apply(Owner.builder().userId(userId).type(USER).build()).resolve(".ssh");
  }
}
