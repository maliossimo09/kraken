package com.octoperf.kraken.git.server.rest;

import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.git.service.api.GitService;
import com.octoperf.kraken.git.service.api.GitUserService;
import com.octoperf.kraken.tests.utils.TestUtils;
import com.octoperf.kraken.tests.web.security.AuthControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.mockito.BDDMockito.given;

public class GitControllerTest extends AuthControllerTest {

  @MockBean
  GitService gitService;
  @MockBean
  GitProjectService projectService;
  @MockBean
  GitUserService gitUserService;

  @BeforeEach
  public void setUp() throws IOException {
    super.setUp();
  }

  @Test
  public void shouldPassTestUtils() {
    TestUtils.shouldPassNPE(GitController.class);
  }

  @Test
  public void shouldExecute() {
    final var command = "cmd";
    given(gitService.execute(userOwner(), command)).willReturn(Mono.empty());

    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/git/execute").build())
        .header("Authorization", "Bearer user-token")
        .body(BodyInserters.fromValue(command))
        .exchange()
        .expectStatus().isOk();
  }
}