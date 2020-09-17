package com.octoperf.kraken.git.service.cmd;

import com.google.common.collect.ImmutableMap;
import com.octoperf.kraken.command.entity.Command;
import com.octoperf.kraken.command.executor.api.CommandService;
import com.octoperf.kraken.git.entity.GitStatus;
import com.octoperf.kraken.git.event.GitRefreshStorageEvent;
import com.octoperf.kraken.git.event.GitStatusUpdateEvent;
import com.octoperf.kraken.git.service.api.GitLogsService;
import com.octoperf.kraken.git.service.api.GitService;
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
    // TODO parse status
//    git --no-optional-locks status --porcelain=v2 --branch
//    http://web.mit.edu/git/www/git-status.html
//    https://github.com/robertgzr/porcelain/blob/master/parse_test.go

//    "#" branch
//    "1" tracked
//    "2" renamed
//    "u" unmerged
//    "?" untracked
//    "!" ignored

// Branch
//    Line                                     Notes
//------------------------------------------------------------
//# branch.oid <commit> | (initial)        Current commit.
//# branch.head <branch> | (detached)      Current branch.
//# branch.upstream <upstream_branch>      If upstream is set.
//# branch.ab +<ahead> -<behind>           If upstream is set and the commit is present

//    1 <XY> <sub> <mH> <mI> <mW> <hH> <hI> <path>

//    2 <XY> <sub> <mH> <mI> <mW> <hH> <hI> <X><score> <path><sep><origPath>
//    Field       Meaning
//--------------------------------------------------------
//<XY>        A 2 character field containing the staged and
//	    unstaged XY values described in the short format,
//	    with unchanged indicated by a "." rather than
//	    a space.
//<sub>       A 4 character field describing the submodule state.
//	    "N..." when the entry is not a submodule.
//	    "S<c><m><u>" when the entry is a submodule.
//	    <c> is "C" if the commit changed; otherwise ".".
//	    <m> is "M" if it has tracked changes; otherwise ".".
//	    <u> is "U" if there are untracked changes; otherwise ".".
//<mH>        The octal file mode in HEAD.
//<mI>        The octal file mode in the index.
//<mW>        The octal file mode in the worktree.
//<hH>        The object name in HEAD.
//<hI>        The object name in the index.
//<X><score>  The rename or copy score (denoting the percentage
//	    of similarity between the source and target of the
//	    move or copy). For example "R100" or "C75".
//<path>      The pathname.  In a renamed/copied entry, this
//	    is the target path.
//<sep>       When the `-z` option is used, the 2 pathnames are separated
//	    with a NUL (ASCII 0x00) byte; otherwise, a tab (ASCII 0x09)
//	    byte separates them.
//<origPath>  The pathname in the commit at HEAD or in the index.
//	    This is only present in a renamed/copied entry, and
//	    tells where the renamed/copied contents came from.
//--------------------------------------------------------

//    u <xy> <sub> <m1> <m2> <m3> <mW> <h1> <h2> <h3> <path>
//    Field       Meaning
//--------------------------------------------------------
//<XY>        A 2 character field describing the conflict type
//	    as described in the short format.
//<sub>       A 4 character field describing the submodule state
//	    as described above.
//<m1>        The octal file mode in stage 1.
//<m2>        The octal file mode in stage 2.
//<m3>        The octal file mode in stage 3.
//<mW>        The octal file mode in the worktree.
//<h1>        The object name in stage 1.
//<h2>        The object name in stage 2.
//<h3>        The object name in stage 3.
//<path>      The pathname.
//--------------------------------------------------------

//    ? <path>

//    ! <path>

//    String[] splited = str.split("\\s+");
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

}
