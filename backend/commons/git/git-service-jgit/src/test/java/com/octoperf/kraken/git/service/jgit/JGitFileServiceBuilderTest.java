package com.octoperf.kraken.git.service.jgit;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.git.entity.command.GitCommand;
import com.octoperf.kraken.git.service.jgit.command.GitCommandExecutor;
import com.octoperf.kraken.security.entity.owner.OwnerTest;
import com.octoperf.kraken.tools.event.bus.EventBus;
import org.assertj.core.api.Assertions;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JGitFileServiceBuilderTest {

  @Mock
  OwnerToTransportConfig ownerToTransportConfig;
  @Mock
  TransportConfigCallback transportConfigCallback;
  @Mock
  OwnerToPath ownerToPath;
  @Mock
  EventBus eventBus;
  @Mock
  GitCommandExecutor commandExecutor;
  @Mock
  Function<Path, Mono<Git>> gitFactory;
  @Mock
  Git git;

  @Test
  void shouldBuild() {
    final var owner = OwnerTest.USER_OWNER;
    final var builder = new JGitFileServiceBuilder(ownerToTransportConfig,
        ownerToPath,
        eventBus,
        ImmutableList.of(commandExecutor),
        gitFactory);
    final var root = Paths.get("testDir");
    given(commandExecutor.getCommandClass()).willReturn(GitCommand.class.getSimpleName());
    given(ownerToPath.apply(owner)).willReturn(root);
    given(gitFactory.apply(root)).willReturn(Mono.just(git));
    given(ownerToTransportConfig.apply(owner)).willReturn(Mono.just(transportConfigCallback));
    Assertions.assertThat(builder.build(owner)).isNotNull();
  }
}