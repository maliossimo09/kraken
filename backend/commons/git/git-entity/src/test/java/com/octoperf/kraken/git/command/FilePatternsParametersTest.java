package com.octoperf.kraken.git.command;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class FilePatternsParametersTest {

  public static final FilePatternsParameters PARAMETERS = FilePatternsParameters.builder()
      .filePatterns(ImmutableList.of("."))
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(PARAMETERS.getClass());
  }

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(PARAMETERS.getClass());
  }

}