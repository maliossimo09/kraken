package com.octoperf.kraken.git.command;

import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class GitCommandLogTest {

  public static final GitCommandLog GIT_COMMAND_LOG = GitCommandLog.builder()
      .owner(Owner.PUBLIC)
      .text("text")
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(GIT_COMMAND_LOG.getClass());
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(GIT_COMMAND_LOG.getClass());
  }

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(GIT_COMMAND_LOG.getClass());
  }
}