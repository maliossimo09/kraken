package com.octoperf.kraken.git.entity.command;

import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class GitRebaseCommandTest {

  public static final GitRebaseCommand COMMAND = GitRebaseCommand.builder()
      .upstream(Optional.empty())
      .preserveMerge(Optional.of(true))
      .strategy(Optional.empty())
      .operation(Optional.of(RebaseOperation.CONTINUE))
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