package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitCommand;
import org.eclipse.jgit.api.Git;
import reactor.core.publisher.Mono;

public interface GitCommandExecutor {

  String getCommandClass();

  Mono<Void> execute(Git git,
                     GitCommand command);

  default boolean refreshStorage() {
    return false;
  }
}
