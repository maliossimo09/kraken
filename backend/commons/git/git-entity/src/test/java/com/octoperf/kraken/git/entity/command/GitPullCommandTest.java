package com.octoperf.kraken.git.entity.command;

import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class GitPullCommandTest {

  public static final GitPullCommand COMMAND = GitPullCommand.builder()
      .strategy(Optional.of(MergeStrategy.RESOLVE))
      .rebase(Optional.of(RebaseMode.PRESERVE))
      .fastForward(Optional.empty())
      .remote(Optional.empty())
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