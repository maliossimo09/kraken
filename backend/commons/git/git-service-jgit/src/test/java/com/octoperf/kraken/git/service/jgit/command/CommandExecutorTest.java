package com.octoperf.kraken.git.service.jgit.command;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {GitAddCommandExecutor.class, GitCommitCommandExecutor.class})
public class CommandExecutorTest {
  @Autowired
  List<GitCommandExecutor> commandExecutors;

  @Test
  void shouldInject() {
    Assertions
        .assertThat(commandExecutors)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2);
  }
}
