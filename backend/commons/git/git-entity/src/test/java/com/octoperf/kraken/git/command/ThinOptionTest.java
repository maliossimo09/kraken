package com.octoperf.kraken.git.command;

import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class ThinOptionTest {

  public static final ThinOption OPTION = ThinOption.builder()
      .thin(true)
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