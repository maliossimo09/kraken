package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitCommand;
import com.octoperf.kraken.git.entity.command.GitPullCommand;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.lib.BranchConfig;
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
    return GitPullCommand.class.getSimpleName();
  }

  @Override
  public Mono<Void> execute(final Git git,
                            final GitCommand command) {
    return Mono.fromCallable(() -> {
      final var pullCommand = (GitPullCommand) command;
      final var pull = git.pull();
      //      TODO Functions to handle this
      pullCommand.getFastForward()
          .map(Enum::name)
          .map(MergeCommand.FastForwardMode::valueOf)
          .ifPresent(pull::setFastForward);
      pullCommand.getRebase()
          .map(Enum::name)
          .map(BranchConfig.BranchRebaseMode::valueOf)
          .ifPresent(pull::setRebase);
      pullCommand.getRemote().ifPresent(pull::setRemote);
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
