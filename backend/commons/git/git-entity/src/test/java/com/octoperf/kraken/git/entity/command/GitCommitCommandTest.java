package com.octoperf.kraken.git.entity.command;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class GitCommitCommandTest {

  public static final GitCommitCommand GIT_COMMIT_COMMAND = GitCommitCommand.builder()
      .message("message")
      .all(Optional.of(true))
      .allowEmpty(Optional.empty())
      .amend(Optional.empty())
      .noVerify(Optional.empty())
      .only(ImmutableList.of())
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(GIT_COMMIT_COMMAND.getClass());
  }

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(GIT_COMMIT_COMMAND.getClass());
  }
}