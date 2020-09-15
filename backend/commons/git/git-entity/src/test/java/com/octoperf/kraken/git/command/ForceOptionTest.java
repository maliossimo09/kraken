package com.octoperf.kraken.git.command;

import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class ForceOptionTest {

  public static final ForceOption OPTION = ForceOption.builder()
      .force(true)
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