package com.octoperf.kraken.git.entity.command;

import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class GitFetchCommandTest {

  public static final GitFetchSubCommand COMMAND = GitFetchSubCommand.builder()
      .remote(Optional.of("master"))
      .forceUpdate(Optional.of(false))
      .dryRun(Optional.empty())
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