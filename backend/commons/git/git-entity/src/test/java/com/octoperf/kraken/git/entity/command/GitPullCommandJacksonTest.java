package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class GitPullCommandJacksonTest {

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void shouldDeSerialize() throws IOException {
    final var object = GitPullCommandTest.COMMAND;
    final String json = mapper.writeValueAsString(object);
    Assertions.assertThat(mapper.readValue(json, GitSubCommand.class)).isEqualTo(object);
  }

  @Test
  public void shouldDeSerializeEmpty() throws IOException {
    Assertions.assertThat(mapper.readValue("{\"type\": \"pull\"}", GitSubCommand.class)).isEqualTo(GitPullSubCommand.builder()
        .strategy(Optional.empty())
        .rebase(Optional.empty())
        .fastForward(Optional.empty())
        .remote(Optional.empty())
        .build());
  }
}
