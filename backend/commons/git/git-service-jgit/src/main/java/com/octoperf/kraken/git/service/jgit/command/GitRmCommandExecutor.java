package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitRmCommand;
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
final class GitRmCommandExecutor implements GitCommandExecutor<GitRmCommand> {

  @Override
  public Class<GitRmCommand> getCommandClass() {
    return GitRmCommand.class;
  }

  @Override
  public Mono<Void> execute(final Git git,
                            final TransportConfigCallback transportConfigCallback,
                            final Path root,
                            final GitRmCommand command) {
    return Mono.fromCallable(() -> {
      final var rm = git.rm();
      command.getFilePatterns().forEach(rm::addFilepattern);
      command.getCached().ifPresent(rm::setCached);
      rm.call();
      return null;
    });
  }

  @Override
  public boolean refreshStorage() {
    return true;
  }
}
