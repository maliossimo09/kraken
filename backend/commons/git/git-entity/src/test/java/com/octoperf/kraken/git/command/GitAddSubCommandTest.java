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
public class GitAddSubCommandTest {

  public static final GitAddSubCommand COMMAND = GitAddSubCommand.builder()
      .filePatterns(ImmutableList.of("pattern"))
      .update(true)
      .build();

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(COMMAND.getClass());
  }

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
    Assertions.assertThat(mapper.readValue("{\"type\": \"add\", \"filePatterns\": []}", GitSubCommand.class)).isEqualTo(GitAddSubCommand.builder()
        .filePatterns(ImmutableList.of())
        .update(null)
        .build());
  }

  @Test
  void shouldParseCommand() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("add", "-u", "file1.txt", "file2.txt").subcommand().commandSpec().userObject())
        .isEqualTo(GitAddSubCommand.builder()
            .filePatterns(ImmutableList.of("file1.txt", "file2.txt"))
            .update(true)
            .build());
  }

  @Test
  void shouldParseCommandNoOption() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("add", "file1.txt").subcommand().commandSpec().userObject())
        .isEqualTo(GitAddSubCommand.builder()
            .filePatterns(ImmutableList.of("file1.txt"))
            .update(null)
            .build());
  }

  @Test
  void shouldParseCommandNoParam() {
    org.junit.jupiter.api.Assertions.assertThrows(CommandLine.MissingParameterException.class, () -> new CommandLine(new GitCommand()).parseArgs("add"));
  }

  @Test
  void shouldParseExistingCommand() {
    new CommandLine(COMMAND).getCommandSpec().args().forEach(argSpec -> System.out.println(argSpec));
  }
}