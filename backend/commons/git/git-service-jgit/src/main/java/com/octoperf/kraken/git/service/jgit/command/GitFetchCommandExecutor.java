package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitSubCommand;
import com.octoperf.kraken.git.entity.command.GitFetchSubCommand;
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
final class GitFetchCommandExecutor implements GitCommandExecutor {

  @Override
  public String getCommandClass() {
    return GitFetchSubCommand.class.getSimpleName();
  }

  @Override
  public Mono<Void> execute(final Git git,
                            final GitSubCommand command) {
    return Mono.fromCallable(() -> {
      final var fetchCommand = (GitFetchSubCommand) command;
      final var fetch = git.fetch();
      fetchCommand.getForceUpdate().ifPresent(fetch::setForceUpdate);
      fetchCommand.getRemote().ifPresent(fetch::setRemote);
      fetchCommand.getDryRun().ifPresent(fetch::setDryRun);
      fetch.call();
      return null;
    });
  }
}
