package com.octoperf.kraken.git.entity.command;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class GitPushCommandTest {

  public static final GitPushSubCommand COMMAND = GitPushSubCommand.builder()
      .remote(Optional.of("HEAD"))
      .force(Optional.of(true))
      .dryRun(Optional.empty())
      .atomic(Optional.empty())
      .options(ImmutableList.of())
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