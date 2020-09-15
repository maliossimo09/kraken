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
public class GitPushSubCommandTest {

  public static final GitPushSubCommand COMMAND = GitPushSubCommand.builder()
      .all(false)
      .atomic(false)
      .dryRun(DryRunOptionTest.OPTION)
      .force(ForceOptionTest.OPTION)
      .pushOptions(ImmutableList.of("option"))
      .refSpecs(ImmutableList.of("refSpec"))
      .remote(RemoteParametersTest.PARAMETERS)
      .tags(false)
      .thin(ThinOptionTest.OPTION)
      .timeout(TimeoutOptionTest.OPTION)
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
    Assertions.assertThat(mapper.readValue("{\"type\": \"push\"}", GitSubCommand.class)).isEqualTo(GitPushSubCommand.builder()
        .build());
  }

  @Test
  void shouldParseCommand() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("push", "--all", "remote", "file1.txt", "file2.txt", "-o", "opt1", "--push-option", "opt2").subcommand().commandSpec().userObject())
        .isEqualTo(GitPushSubCommand.builder()
            .all(true)
            .refSpecs(ImmutableList.of("file1.txt", "file2.txt"))
            .pushOptions(ImmutableList.of("opt1", "opt2"))
            .remote(RemoteParameters.builder().remote("remote").build())
            .build());
  }

  @Test
  void shouldParseCommandNoOption() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("push").subcommand().commandSpec().userObject())
        .isEqualTo(GitPushSubCommand.builder()
            .remote(RemoteParameters.builder().remote("origin").build())
            .build());
  }

}