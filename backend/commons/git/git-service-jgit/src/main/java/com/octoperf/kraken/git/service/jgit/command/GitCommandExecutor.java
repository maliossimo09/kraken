package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import reactor.core.publisher.Mono;

public interface GitCommandExecutor {

  String getCommandClass();

  Mono<Void> execute(Git git,
                     TransportConfigCallback transportConfigCallback,
                     GitCommand command);

  default boolean refreshStorage() {
    return false;
  }
}
