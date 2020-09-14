package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.command.GitPullSubCommand;
import com.octoperf.kraken.git.command.GitSubCommand;
import lombok.AllArgsConstructor;
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
final class GitPullCommandExecutor implements GitCommandExecutor {

  @Override
  public String getCommandClass() {
    return GitPullSubCommand.class.getSimpleName();
  }

  @Override
  public Mono<Void> execute(final Git git,
                            final GitSubCommand command) {
    return Mono.fromCallable(() -> {
      final var pullCommand = (GitPullSubCommand) command;
      final var pull = git.pull();
      git.rm().setCached()
      //      TODO Functions to handle this
//      pull.setTagOpt()
//      pull.setRebase()
//      pullCommand.getFastForward()
//          .map(Enum::name)
//          .map(MergeCommand.FastForwardMode::valueOf)
//          .ifPresent(pull::setFastForward);
//      pullCommand.getRebase()
//          .map(Enum::name)
//          .map(BranchConfig.BranchRebaseMode::valueOf)
//          .ifPresent(pull::setRebase);
//      pullCommand.getRemote().ifPresent(pull::setRemote);
//      TODO handle this
//      pullCommand.getStrategy()
//          .map(Enum::name)
//          .ifPresent(pull::setStrategy);
      pull.call();
      return null;
    });
  }

  @Override
  public boolean refreshStorage() {
    return true;
  }
}
