package com.octoperf.kraken.git.service.jgit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RepositoryBuilderSupplierTest {

  @Test
  void shouldGet() {
    Assertions.assertThat(new RepositoryBuilderSupplier().get()).isNotNull();
  }
}