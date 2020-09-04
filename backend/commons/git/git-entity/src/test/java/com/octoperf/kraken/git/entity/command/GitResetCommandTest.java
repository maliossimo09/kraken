package com.octoperf.kraken.git.entity.command;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class GitResetCommandTest {

  public static final GitResetCommand COMMAND = GitResetCommand.builder()
      .ref(Optional.empty())
      .resetType(Optional.of(ResetType.HARD))
      .disableRefLog(Optional.empty())
      .path(ImmutableList.of())
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