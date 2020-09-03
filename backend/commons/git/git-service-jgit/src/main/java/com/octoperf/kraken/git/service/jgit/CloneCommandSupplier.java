package com.octoperf.kraken.git.service.jgit;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
final class CloneCommandSupplier implements Supplier<CloneCommand> {

  @Override
  public CloneCommand get() {
    return new CloneCommand();
  }
}
