package com.octoperf.kraken.git.service.jgit;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.function.Function;

@Slf4j
@Component
final class GitFactory implements Function<Path, Mono<Git>> {

  @Override
  public Mono<Git> apply(final Path root) {
    return Mono.fromCallable(() -> Git.open(root.toFile()));
  }
}
