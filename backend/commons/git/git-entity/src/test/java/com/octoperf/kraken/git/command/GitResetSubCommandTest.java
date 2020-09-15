package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.Application;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import picocli.CommandLine;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class GitResetSubCommandTest {

  public static final GitResetSubCommand COMMAND = GitResetSubCommand.builder()
      .commit("commit")
      .hard(true)
      .mixed(false)
      .soft(false)
      .paths(ImmutableList.of("path1", "path2"))
      .build();

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(COMMAND.getClass());
  }

  @Test
  public void shouldDeSerialize() throws IOException {
    final var object = COMMAND;
    final String json = mapper.writeValueAsString(object);
    Assertions.assertThat(mapper.readValue(json, GitSubCommand.class)).isEqualTo(object);
  }

  @Test
  public void shouldDeSerializeEmpty() throws IOException {
    Assertions.assertThat(mapper.readValue("{\"type\": \"reset\"}", GitSubCommand.class)).isEqualTo(GitResetSubCommand.builder()
        .build());
  }

  @Test
  void shouldParseCommand() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("reset", "--hard", "commit", "--", "path1", "path2").subcommand().commandSpec().userObject())
        .isEqualTo(GitResetSubCommand.builder()
            .hard(true)
            .commit("commit")
            .paths(ImmutableList.of("path1", "path2"))
            .build());
  }

  @Test
  void shouldParseCommandNoParam() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("reset").subcommand().commandSpec().userObject())
        .isEqualTo(GitResetSubCommand.builder()
            .build());
  }
}