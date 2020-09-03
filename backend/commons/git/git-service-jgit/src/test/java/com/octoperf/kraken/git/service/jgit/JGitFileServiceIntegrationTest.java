package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.Application;
import com.octoperf.kraken.config.api.ApplicationProperties;
import com.octoperf.kraken.security.authentication.api.AuthenticationMode;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import com.octoperf.kraken.security.authentication.api.UserProviderFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static com.octoperf.kraken.git.service.jgit.JGitProjectServiceIntegrationTest.OWNER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Tag("integration")
public class JGitFileServiceIntegrationTest {

  @Autowired
  JGitFileServiceBuilder gitFileServiceBuilder;

  @MockBean
  ApplicationProperties properties;

  @MockBean
  UserProviderFactory userProviderFactory;
  @MockBean
  UserProvider userProvider;

  JGitFileService gitFileService;

  @BeforeEach
  public void before() {
    given(properties.getData()).willReturn("/home/ubuntu/kraken/gitTest/");
    given(userProviderFactory.getMode()).willReturn(AuthenticationMode.SESSION);
    given(userProviderFactory.create("")).willReturn(userProvider);
    given(userProvider.getOwner(any(), any())).willReturn(Mono.just(OWNER));
    gitFileService = (JGitFileService) gitFileServiceBuilder.build(OWNER).block();
  }

  @AfterEach
  public void after() {
    this.shouldStatus();
    gitFileService.close();
  }

  @Test
  void shouldStatus() {
    final var status = gitFileService.status().block();
    Assertions.assertThat(status).isNotNull();
    System.out.println(status);
  }

  // TODO init local repository without remote pour test log et cat

//  @Test
//  void shouldAdd() {
//    StepVerifier.create(gitFileService.add(Optional.of("README.md")))
//        .expectComplete();
//  }
//
//  @Test
//  void shouldCommit() {
//    StepVerifier.create(gitFileService.commit("Update"))
//        .expectComplete();
//  }

  @Test
  void shouldLog() {
    final var ids = gitFileService.log("README.md").collectList().block();
    System.out.println(ids);
  }

}
