package com.octoperf.kraken.git.entity.command;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class GitAddCommandTest {

  public static final GitAddCommand GIT_ADD_COMMAND = GitAddCommand.builder()
      .filePatterns(ImmutableList.of("pattern"))
      .update(Optional.of(true))
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(GIT_ADD_COMMAND.getClass());
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(GIT_ADD_COMMAND.getClass());
  }

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(GIT_ADD_COMMAND.getClass());
  }
}