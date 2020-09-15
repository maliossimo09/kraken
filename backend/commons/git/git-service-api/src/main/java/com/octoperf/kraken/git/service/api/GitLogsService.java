package com.octoperf.kraken.git.service.api;

import com.octoperf.kraken.git.command.GitCommandLog;
import com.octoperf.kraken.security.entity.owner.Owner;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public interface GitLogsService {

  Flux<GitCommandLog> listen(Owner owner);

  Disposable push(Owner owner, Flux<String> logs);

  void add(Owner owner, String text);

  void clear();

}
