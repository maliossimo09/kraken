package com.octoperf.kraken.git.command;

import com.octoperf.kraken.Application;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class MergeStrategyOptionTest {

  public static final MergeStrategyOption OPTION = MergeStrategyOption.builder()
      .build();


  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(OPTION.getClass());
  }

}