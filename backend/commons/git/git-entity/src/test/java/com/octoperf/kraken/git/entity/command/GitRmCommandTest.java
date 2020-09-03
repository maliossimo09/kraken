package com.octoperf.kraken.git.entity.command;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class GitRmCommandTest {

  public static final GitRmCommand GIT_RM_COMMAND = GitRmCommand.builder()
      .filePatterns(ImmutableList.of("pattern"))
      .cached(Optional.of(true))
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(GIT_RM_COMMAND.getClass());
  }

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(GIT_RM_COMMAND.getClass());
  }
}