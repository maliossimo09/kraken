package com.octoperf.kraken.git.service.api;

import com.octoperf.kraken.git.entity.GitLog;
import com.octoperf.kraken.git.entity.GitStatus;
import com.octoperf.kraken.git.command.GitSubCommand;
import com.octoperf.kraken.git.event.GitRefreshStorageEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GitFileService extends AutoCloseable {

  Mono<Void> execute(String command);

  Mono<Void> execute(GitSubCommand command);

  Mono<List<GitLog>> log(String path);

  Mono<String> cat(GitLog log);

  Mono<GitStatus> status();

  Flux<GitStatus> watchStatus();

  Flux<GitRefreshStorageEvent> watchRefresh();
}
