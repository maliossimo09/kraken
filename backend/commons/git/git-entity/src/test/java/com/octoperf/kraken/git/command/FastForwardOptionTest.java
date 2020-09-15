package com.octoperf.kraken.git.command;

import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class FastForwardOptionTest {

  public static final FastForwardOption OPTION = FastForwardOption.builder()
      .ff(true)
      .ffOnly(false)
      .noFf(null)
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