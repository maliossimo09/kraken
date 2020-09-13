package com.octoperf.kraken.git.entity.command;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class GitCommitCommandTest {

  public static final GitCommitSubCommand COMMAND = GitCommitSubCommand.builder()
      .message("message")
      .all(Optional.of(true))
      .allowEmpty(Optional.empty())
      .amend(Optional.empty())
      .noVerify(Optional.empty())
      .only(ImmutableList.of("only"))
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