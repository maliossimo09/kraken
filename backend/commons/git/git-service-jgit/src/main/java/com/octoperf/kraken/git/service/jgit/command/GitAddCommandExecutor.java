package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitAddCommand;
import com.octoperf.kraken.git.entity.command.GitCommand;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class GitAddCommandExecutor implements GitCommandExecutor {

  @Override
  public String getCommandClass() {
    return GitAddCommand.class.getSimpleName();
  }

  @Override
  public Mono<Void> execute(final Git git,
                            final TransportConfigCallback transportConfigCallback,
                            final Path root,
                            final GitCommand command) {
    return Mono.fromCallable(() -> {
      final var addCommand = (GitAddCommand) command;
      final var add = git.add();
      addCommand.getFilePatterns().forEach(add::addFilepattern);
      addCommand.getUpdate().ifPresent(add::setUpdate);
      add.call();
      return null;
    });
  }
}
