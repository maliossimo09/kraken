package com.octoperf.kraken.git.command;

import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class DryRunOptionTest {

  public static final DryRunOption OPTION = DryRunOption.builder()
      .dryRun(true)
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(OPTION.getClass());
  }

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(OPTION.getClass());
  }

}