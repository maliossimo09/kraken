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
import com.octoperf.kraken.git.service.cmd.parser.GitStatusParser;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.tools.environment.KrakenEnvironmentKeys;
import com.octoperf.kraken.tools.event.bus.EventBus;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.google.common.base.Preconditions.checkArgument;
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
  @NonNull UserIdToCommandEnvironment toCommandEnvironment;
  @NonNull GitStatusParser statusParser;
  @NonNull EventBus eventBus;

  @Override
  public Mono<Void> execute(final Owner owner, final String command) {
    return commandService.parseCommandLine(command)
        .flatMap(args -> Mono.fromCallable(() -> {
          checkArgument(args.size() >= 2);
          checkArgument("git".equals(args.get(0)), "Only git commands are supported.");
          final var subCommand = GitSubCommand.valueOf(args.get(1));
          final var env = subCommand.isRemote() ? toCommandEnvironment.apply(owner.getUserId()) : ImmutableMap.<KrakenEnvironmentKeys, String>of();
          final var cmd = Command.builder()
              .args(args)
              .path(ownerToPath.apply(owner).toString())
              .environment(env)
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
    final var commandStatus = Command.builder()
        .args(ImmutableList.of("git", "--no-optional-locks", "status", "--porcelain=v2", "--branch"))
        .path(ownerToPath.apply(owner).toString())
        .environment(ImmutableMap.of())
        .build();
    return statusParser.apply(commandService.validate(commandStatus).flatMapMany(commandService::execute));
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

}
