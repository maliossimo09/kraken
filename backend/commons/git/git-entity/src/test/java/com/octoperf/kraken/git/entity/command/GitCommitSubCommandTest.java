package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class GitCommitSubCommandTest {

  public static final GitCommitSubCommand COMMAND = GitCommitSubCommand.builder()
      .message("message")
      .all(true)
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
    Assertions.assertThat(mapper.readValue("{\"type\": \"commit\", \"message\": \"message\"}", GitSubCommand.class)).isEqualTo(GitCommitSubCommand.builder()
        .message("message")
        .build());
  }

  @Test
  void shouldParseCommand() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("commit", "-m", "message", "-a", "--amend").subcommand().commandSpec().userObject())
        .isEqualTo(GitCommitSubCommand.builder()
            .message("message")
            .all(true)
            .amend(true)
            .build());
  }

  @Test
  void shouldParseCommandNoParam() {
    org.junit.jupiter.api.Assertions.assertThrows(CommandLine.MissingParameterException.class, () -> new CommandLine(new GitCommand()).parseArgs("commit"));
  }
}