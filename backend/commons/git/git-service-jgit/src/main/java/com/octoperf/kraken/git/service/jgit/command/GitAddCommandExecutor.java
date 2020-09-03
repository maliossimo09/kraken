package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitAddCommand;
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
final class GitAddCommandExecutor implements GitCommandExecutor<GitAddCommand> {

  @Override
  public Class<GitAddCommand> getCommandClass() {
    return GitAddCommand.class;
  }

  @Override
  public Mono<Void> execute(final Git git,
                            final TransportConfigCallback transportConfigCallback,
                            final Path root,
                            final GitAddCommand command) {
    return Mono.fromCallable(() -> {
      final var add = git.add();
      command.getFilePatterns().forEach(add::addFilepattern);
      command.getUpdate().ifPresent(add::setUpdate);
      add.call();
      return null;
    });
  }
}
