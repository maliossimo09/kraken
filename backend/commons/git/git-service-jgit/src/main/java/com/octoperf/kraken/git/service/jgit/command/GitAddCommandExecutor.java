package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.command.GitAddSubCommand;
import com.octoperf.kraken.git.command.GitSubCommand;
import com.octoperf.kraken.git.service.api.GitLogsService;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class GitAddCommandExecutor implements GitCommandExecutor {

  @NonNull GitLogsService logsService;

  @Override
  public String getCommandClass() {
    return GitAddSubCommand.class.getSimpleName();
  }

  @Override
  public Mono<Void> execute(final Owner owner,
                            final Git git,
                            final GitSubCommand command) {
    return Mono.fromCallable(() -> {
      final var addCommand = (GitAddSubCommand) command;
      final var cmd = git.add();
      addCommand.getFilePatterns().getFilePatterns().forEach(cmd::addFilepattern);
      Optional.ofNullable(addCommand.getUpdate()).ifPresent(cmd::setUpdate);
      cmd.call();
      logsService.add(owner, "Add done");
      return null;
    });
  }
}
