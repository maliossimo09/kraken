package com.octoperf.kraken.git.service.cmd.parser;

import com.octoperf.kraken.git.entity.GitStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class SpringGitStatusParser implements GitStatusParser{

  @NonNull Map<Character, GitStatusLineParser> parsers;

  @Override
  public Mono<GitStatus> apply(Flux<String> stringFlux) {
    stringFlux.map(line -> line.split("\\s+"));
    // TODO reduce using parsers
    return null;
  }
}
