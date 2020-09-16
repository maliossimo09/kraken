package com.octoperf.kraken.command.executor.api;

import com.octoperf.kraken.command.entity.Command;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CommandService {

  Flux<String> execute(Command command);

  Mono<List<String>> parseCommandLine(String commandLine);

}
