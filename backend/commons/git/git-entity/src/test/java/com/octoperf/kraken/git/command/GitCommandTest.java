package com.octoperf.kraken.git.command;

import com.octoperf.kraken.Application;
import com.octoperf.kraken.tests.utils.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class GitCommandTest {

  public static final GitCommand COMMAND = GitCommand.builder().build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(COMMAND.getClass());
  }

  @Test
  public void shouldPassString() {
    TestUtils.shouldPassToString(COMMAND.getClass());
  }

  @Test
  void shouldDisplayHelp() {
    final var os = new ByteArrayOutputStream();
    new CommandLine(new GitCommand()).usage(new PrintStream(os, true));
    final var help = new String(os.toByteArray(), StandardCharsets.UTF_8);
    System.out.println(help);
    Assertions.assertThat(help)
        .isNotNull()
        .isNotEmpty();
  }

  @Test
  void shouldDisplaySubHelp() {
    final var os = new ByteArrayOutputStream();
    new CommandLine(new GitAddSubCommand()).usage(new PrintStream(os, true));
    final var help = new String(os.toByteArray(), StandardCharsets.UTF_8);
    System.out.println(help);
    Assertions.assertThat(help)
        .isNotNull()
        .isNotEmpty();
  }
}