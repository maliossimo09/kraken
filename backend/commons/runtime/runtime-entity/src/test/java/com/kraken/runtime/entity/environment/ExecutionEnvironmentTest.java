package com.kraken.runtime.entity.environment;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.NullPointerTester;
import com.kraken.test.utils.TestUtils;
import org.junit.Test;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;

public class ExecutionEnvironmentTest {

  public static final ExecutionEnvironment EXECUTION_ENVIRONMENT = ExecutionEnvironment.builder()
      .taskType("RUN")
      .description("description")
      .hostIds(ImmutableList.of("local"))
      .entries(ImmutableList.of(ExecutionEnvironmentEntryTest.EXECUTION_ENVIRONMENT_ENTRY))
      .build();


  @Test
  public void shouldPassTestUtils() {
    TestUtils.shouldPassEquals(EXECUTION_ENVIRONMENT.getClass());
    new NullPointerTester().setDefault(ExecutionEnvironmentEntry.class, ExecutionEnvironmentEntryTest.EXECUTION_ENVIRONMENT_ENTRY).testConstructors(EXECUTION_ENVIRONMENT.getClass(), PACKAGE);
    TestUtils.shouldPassToString(EXECUTION_ENVIRONMENT);
  }

}
