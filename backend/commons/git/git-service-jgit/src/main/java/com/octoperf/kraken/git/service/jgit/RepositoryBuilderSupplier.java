package com.octoperf.kraken.git.service.jgit;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
final class RepositoryBuilderSupplier implements Supplier<FileRepositoryBuilder> {

  @Override
  public FileRepositoryBuilder get() {
    return new FileRepositoryBuilder();
  }
}
