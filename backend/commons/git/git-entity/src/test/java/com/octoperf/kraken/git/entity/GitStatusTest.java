package com.octoperf.kraken.git.entity;

import com.google.common.collect.ImmutableMultimap;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

class GitStatusTest {

  public static final GitStatus GIT_STATUS = GitStatus.builder()
      .diff(ImmutableMultimap.of("path", GitFileStatus.CONFLICTING, "path", GitFileStatus.MODIFIED))
      .hasUncommittedChanges(true)
      .isClean(false)
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(GIT_STATUS.getClass());
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(GIT_STATUS.getClass());
  }

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(GIT_STATUS.getClass());
  }
}