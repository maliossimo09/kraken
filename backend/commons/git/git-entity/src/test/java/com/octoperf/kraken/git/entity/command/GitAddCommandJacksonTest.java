package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.Application;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class GitAddCommandJacksonTest {

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void shouldDeSerialize() throws IOException {
    final var object = GitAddCommandTest.GIT_ADD_COMMAND;
    final String json = mapper.writeValueAsString(object);
    Assertions.assertThat(mapper.readValue(json, GitAddCommand.class)).isEqualTo(object);
  }

  @Test
  public void shouldDeSerializeEmpty() throws IOException {
    Assertions.assertThat(mapper.readValue("{}", GitAddCommand.class)).isEqualTo(GitAddCommand.builder()
        .filePatterns(ImmutableList.of())
        .update(Optional.empty())
        .build());
  }
}
