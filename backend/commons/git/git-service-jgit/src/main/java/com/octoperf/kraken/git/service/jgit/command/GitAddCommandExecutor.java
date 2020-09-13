package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitAddSubCommand;
import com.octoperf.kraken.git.entity.command.GitSubCommand;
import lombok.AllArgsConstructor;
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

  @Override
  public String getCommandClass() {
    return GitAddSubCommand.class.getSimpleName();
  }

  @Override
  public Mono<Void> execute(final Git git,
                            final GitSubCommand command) {
    return Mono.fromCallable(() -> {
      final var addCommand = (GitAddSubCommand) command;
      final var add = git.add();
      addCommand.getFilePatterns().forEach(add::addFilepattern);
      Optional.ofNullable(addCommand.getUpdate()).ifPresent(add::setUpdate);
      add.call();
      return null;
    });
  }
}
