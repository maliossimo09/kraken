package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.git.entity.GitCredentialsTest;
import com.octoperf.kraken.git.service.api.GitUserService;
import com.octoperf.kraken.security.entity.owner.OwnerTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class SpringOwnerToTransportConfigTest {

  @Mock
  GitUserService userService;

  @Test
  void shouldApply() {
    final var toTransport = new SpringOwnerToTransportConfig(userService);
    final var owner = OwnerTest.USER_OWNER;
    BDDMockito.given(userService.getCredentials(owner.getUserId())).willReturn(Mono.just(GitCredentialsTest.GIT_CREDENTIALS));
    Assertions.assertThat(toTransport.apply(owner).block()).isNotNull();
  }
}