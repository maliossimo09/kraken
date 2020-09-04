package com.octoperf.kraken.git.entity.command;

import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class GitMergeCommandTest {

  public static final GitMergeCommand COMMAND = GitMergeCommand.builder()
      .message(Optional.of("message"))
      .squash(Optional.of(false))
      .fastForward(Optional.of(FastForwardMode.NO_FF))
      .commit(Optional.empty())
      .strategy(Optional.empty())
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