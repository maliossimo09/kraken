package com.octoperf.kraken.git.server.rest;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.git.entity.GitLog;
import com.octoperf.kraken.git.entity.GitLogTest;
import com.octoperf.kraken.git.entity.command.GitAddCommandTest;
import com.octoperf.kraken.git.service.api.GitFileService;
import com.octoperf.kraken.git.service.api.GitFileServiceBuilder;
import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.git.service.api.GitUserService;
import com.octoperf.kraken.tests.utils.TestUtils;
import com.octoperf.kraken.tests.web.security.AuthControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class GitFileControllerTest extends AuthControllerTest {

  @MockBean
  GitFileServiceBuilder fileServiceBuilder;
  @MockBean
  GitFileService gitFileService;
  @MockBean
  GitProjectService projectService;
  @MockBean
  GitUserService gitUserService;

  @BeforeEach
  public void setUp() throws IOException {
    super.setUp();
    given(fileServiceBuilder.build(any())).willReturn(Mono.just(gitFileService));
  }

  @Test
  public void shouldPassTestUtils() {
    TestUtils.shouldPassNPE(GitFileController.class);
  }

  @Test
  public void shouldExecute() {
    final var command = GitAddCommandTest.GIT_ADD_COMMAND;
    given(gitFileService.execute(command)).willReturn(Mono.empty());

    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/git/execute").build())
        .header("Authorization", "Bearer user-token")
        .body(BodyInserters.fromValue(command))
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  public void shouldLog() {
    final var log = GitLogTest.GIT_LOG;
    final var path = "path";
    given(gitFileService.log(path)).willReturn(Mono.just(ImmutableList.of(log)));

    webTestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/git/log")
            .queryParam("path", path)
            .build())
        .header("Authorization", "Bearer user-token")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(GitLog.class)
        .hasSize(1)
        .contains(log);
  }

  @Test
  public void shouldCat() {
    final var log = GitLogTest.GIT_LOG;
    final var content = "content";
    given(gitFileService.cat(log)).willReturn(Mono.just(content));

    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/git/cat").build())
        .header("Authorization", "Bearer user-token")
        .body(BodyInserters.fromValue(log))
        .exchange()
        .expectHeader()
        .contentType("text/plain;charset=UTF-8")
        .expectStatus().isOk()
        .expectBody(String.class)
        .isEqualTo(content);
  }
}