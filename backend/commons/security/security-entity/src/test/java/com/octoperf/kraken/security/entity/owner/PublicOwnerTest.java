package com.octoperf.kraken.security.entity.owner;

import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class PublicOwnerTest {

  public static final PublicOwner PUBLIC_OWNER = PublicOwner.INSTANCE;

  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(PUBLIC_OWNER.getClass());
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(PUBLIC_OWNER.getClass());
  }

  @Test
  public void shouldPassToString() {
    TestUtils.shouldPassToString(PUBLIC_OWNER);
  }

}
