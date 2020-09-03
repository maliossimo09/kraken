package com.octoperf.kraken.git.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

class GitStatusTest {

  public static final GitStatus GIT_STATUS = GitStatus.builder()
      .repositoryState("repositoryState")
      .repositoryStateDescription("repositoryStateDescription")
      .diff(ImmutableMultimap.of("path", GitFileStatus.CONFLICTING, "path", GitFileStatus.MODIFIED))
      .conflicts(ImmutableMap.of("key", "value"))
      .hasUncommittedChanges(true)
      .clean(false)
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