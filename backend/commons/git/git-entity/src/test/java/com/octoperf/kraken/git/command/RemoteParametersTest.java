package com.octoperf.kraken.git.command;

import com.octoperf.kraken.Application;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class RemoteParametersTest {

  public static final RemoteParameters PARAMETERS = RemoteParameters.builder().remote("origin")
      .build();


  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(PARAMETERS.getClass());
  }

}