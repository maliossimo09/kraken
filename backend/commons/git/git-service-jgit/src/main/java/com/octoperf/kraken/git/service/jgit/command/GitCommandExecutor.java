package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

public interface GitCommandExecutor<T extends GitCommand> {

  Class<T> getCommandClass();

  Mono<Void> execute(Git git,
                     TransportConfigCallback transportConfigCallback,
                     Path root,
                     T command);

  default boolean refreshStorage() {
    return false;
  }
}
