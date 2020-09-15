package com.octoperf.kraken.git.command;

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
public class GitRebaseSubCommandTest {

  public static final GitRebaseSubCommand COMMAND = GitRebaseSubCommand.builder()
      .onto("onto")
      .operation("continue")
      .preserveMerges(true)
      .strategy(MergeStrategyOptionTest.OPTION)
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
    Assertions.assertThat(mapper.readValue("{\"type\": \"rebase\"}", GitSubCommand.class)).isEqualTo(GitRebaseSubCommand.builder()
        .build());
  }

  @Test
  void shouldParseCommand() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("rebase", "--operation", "begin", "-s", "ours").subcommand().commandSpec().userObject())
        .isEqualTo(GitRebaseSubCommand.builder()
            .operation("begin")
            .strategy(MergeStrategyOption.builder().strategyName("ours").build())
            .build());
  }

  @Test
  void shouldParseCommandNoParam() {
    Assertions.assertThat(new CommandLine(new GitCommand()).parseArgs("rebase").subcommand().commandSpec().userObject())
        .isEqualTo(GitRebaseSubCommand.builder()
            .build());
  }
}