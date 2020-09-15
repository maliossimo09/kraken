package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.command.GitSubCommand;
import com.octoperf.kraken.security.entity.owner.Owner;
import org.eclipse.jgit.api.Git;
import reactor.core.publisher.Mono;

public interface GitCommandExecutor {

  String getCommandClass();

  Mono<Void> execute(Owner owner,
                     Git git,
                     GitSubCommand command);

  default boolean refreshStorage() {
    return false;
  }
}
