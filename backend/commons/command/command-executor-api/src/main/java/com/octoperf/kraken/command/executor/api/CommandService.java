package com.octoperf.kraken.command.executor.api;

import com.octoperf.kraken.command.entity.Command;
import reactor.core.publisher.Flux;

public interface CommandService {

  Flux<String> execute(Command command);

}
