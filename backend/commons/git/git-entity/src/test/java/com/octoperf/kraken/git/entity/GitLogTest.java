package com.octoperf.kraken.git.entity;

import com.google.common.testing.NullPointerTester;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class GitLogTest {

  public static final GitLog GIT_LOG = GitLog.builder()
      .author(GitIdentityTest.GIT_IDENTITY)
      .committer(GitIdentityTest.GIT_IDENTITY)
      .encoding("encoding")
      .id("id")
      .message("message")
      .path("path")
      .time(42L)
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(GIT_LOG.getClass());
  }

  @Test
  public void shouldPassNPE() {
    new NullPointerTester()
        .setDefault(GitIdentity.class, GitIdentityTest.GIT_IDENTITY)
        .testConstructors(GitLog.class, NullPointerTester.Visibility.PACKAGE);
  }

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(GIT_LOG.getClass());
  }
}