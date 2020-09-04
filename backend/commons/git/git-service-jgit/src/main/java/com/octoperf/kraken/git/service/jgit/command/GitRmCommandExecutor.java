package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitCommand;
import com.octoperf.kraken.git.entity.command.GitRmCommand;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class GitRmCommandExecutor implements GitCommandExecutor {

  @Override
  public String getCommandClass() {
    return GitRmCommand.class.getSimpleName();
  }

  @Override
  public Mono<Void> execute(final Git git,
                            final TransportConfigCallback transportConfigCallback,
                            final GitCommand command) {
    return Mono.fromCallable(() -> {
      final var rmCommand = (GitRmCommand) command;
      final var rm = git.rm();
      rmCommand.getFilePatterns().forEach(rm::addFilepattern);
      rmCommand.getCached().ifPresent(rm::setCached);
      rm.call();
      return null;
    });
  }

  @Override
  public boolean refreshStorage() {
    return true;
  }
}
