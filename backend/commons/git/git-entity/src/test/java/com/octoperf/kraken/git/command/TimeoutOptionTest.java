package com.octoperf.kraken.git.command;

import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class TimeoutOptionTest {

  public static final TimeoutOption OPTION = TimeoutOption.builder()
      .timeout(42)
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