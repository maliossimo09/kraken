package com.octoperf.kraken.git.client.spring;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.Application;
import com.octoperf.kraken.git.client.api.GitClient;
import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.git.entity.GitStatusTest;
import com.octoperf.kraken.git.event.GitRefreshStorageEventTest;
import com.octoperf.kraken.git.service.api.GitFileService;
import com.octoperf.kraken.git.service.api.GitFileServiceBuilder;
import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.security.authentication.client.api.AuthenticatedClientBuildOrder;
import com.octoperf.kraken.security.entity.owner.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class SpringGitClientTest {

  private GitClient client;

  @MockBean
  GitProjectService projectService;
  @MockBean
  GitFileServiceBuilder fileServiceBuilder;
  @MockBean
  GitFileService fileService;

  @BeforeEach
  public void before() {
    given(fileServiceBuilder.build(Owner.PUBLIC)).willReturn(Mono.just(fileService));
    client = new SpringGitClientBuilder(ImmutableList.of(), projectService, fileServiceBuilder).build(AuthenticatedClientBuildOrder.NOOP).block();
  }

  @Test
  public void shouldConnect() {
    final var url = "url";
    final var config = GitConfiguration.builder().repositoryUrl(url).build();
    given(projectService.connect(Owner.PUBLIC, url)).willReturn(Mono.just(config));
    final var response = client.connect(url).block();
    assertThat(response).isNotNull().isEqualTo(config);
  }

  @Test
  public void shouldWatchStatus() {
    given(fileService.watchStatus()).willReturn(Flux.just(GitStatusTest.GIT_STATUS));
    final var response = client.watchStatus().collectList().block();
    assertThat(response).isNotNull().hasSize(1);
  }

  @Test
  public void shouldWatchRefresh() {
    given(fileService.watchRefresh()).willReturn(Flux.just(GitRefreshStorageEventTest.GIT_REFRESH_STORAGE_EVENT));
    final var response = client.watchRefresh().collectList().block();
    assertThat(response).isNotNull().hasSize(1);
  }
}
