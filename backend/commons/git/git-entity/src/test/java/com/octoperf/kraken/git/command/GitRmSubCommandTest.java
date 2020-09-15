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
public class GitRmSubCommandTest {

  public static final GitRmSubCommand COMMAND = GitRmSubCommand.builder()
      .filePatterns(FilePatternsParametersTest.PARAMETERS)
      .cached(true)
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
    Assertions.assertThat(mapper.readValue("{\"type\": \"rm\"}", GitSubCommand.class)).isEqualTo(GitRmSubCommand.builder()
        .filePatterns(FilePatternsParameters.builder().build())
        .cached(null)
        .build());
  }

  @Test
  void shouldParseCommand() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("rm", "--cached", "file1.txt", "file2.txt").subcommand().commandSpec().userObject())
        .isEqualTo(GitRmSubCommand.builder()
            .filePatterns(FilePatternsParameters.builder().filePatterns(ImmutableList.of("file1.txt", "file2.txt")).build())
            .cached(true)
            .build());
  }

  @Test
  void shouldParseCommandNoOption() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("rm", "file1.txt").subcommand().commandSpec().userObject())
        .isEqualTo(GitRmSubCommand.builder()
            .filePatterns(FilePatternsParameters.builder().filePatterns(ImmutableList.of("file1.txt")).build())
            .cached(null)
            .build());
  }

  @Test
  void shouldParseCommandNoParam() {
    org.junit.jupiter.api.Assertions.assertThrows(CommandLine.MissingParameterException.class, () -> new CommandLine(new GitCommand()).parseArgs("rm"));
  }

}