package com.octoperf.kraken.git.entity.command;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class GitAddCommandTest {

  public static final GitAddCommand COMMAND = GitAddCommand.builder()
      .filePatterns(ImmutableList.of("pattern"))
      .update(Optional.of(true))
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(COMMAND.getClass());
  }

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(COMMAND.getClass());
  }
}