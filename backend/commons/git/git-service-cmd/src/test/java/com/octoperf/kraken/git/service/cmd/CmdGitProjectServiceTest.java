package com.octoperf.kraken.git.service.cmd;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.octoperf.kraken.command.entity.Command;
import com.octoperf.kraken.command.executor.api.CommandService;
import com.octoperf.kraken.config.api.ApplicationProperties;
import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.security.entity.owner.OwnerTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CmdGitProjectServiceTest {

  @Mock
  OwnerToPath ownerToPath;
  @Mock
  CommandService commandService;
  @Mock
  ApplicationProperties properties;
  @Mock
  UserIdToCommandEnvironment toCommandEnvironment;
  @Captor
  ArgumentCaptor<Command> commandCaptor;

  CmdGitProjectService projectService;

  @BeforeEach
  public void beforeEach() {
    projectService = new CmdGitProjectService(ownerToPath, commandService, properties, toCommandEnvironment);
  }

  @Test
  void shouldConnect() {
    final var owner = OwnerTest.USER_OWNER;
    final var repositoryUrl = "repoUrl";
    final var rootPath = Paths.get("testDir");
    given(ownerToPath.apply(owner)).willReturn(rootPath);
    given(commandService.validate(any(Command.class))).willAnswer(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, Command.class)));
    given(commandService.execute(anyList())).willReturn(Flux.just(repositoryUrl));

    final var config = projectService.connect(owner, repositoryUrl).block();
    Assertions.assertThat(config)
        .isNotNull()
        .isEqualTo(GitConfiguration.builder().repositoryUrl(repositoryUrl).build());

    verify(commandService, times(2)).execute(commandCaptor.capture());

    Assertions.assertThat(commandCaptor.getAllValues()).hasSize(2);
    final var cloneCmd = commandCaptor.getAllValues().get(1);
    Assertions.assertThat(cloneCmd.getPath()).isEqualTo(rootPath.toString());
    Assertions.assertThat(cloneCmd.getArgs().subList(0, cloneCmd.getArgs().size() - 1)).isEqualTo(ImmutableList.of("git", "clone", repositoryUrl));

    final var confCmd = commandCaptor.getAllValues().get(0);
    Assertions.assertThat(confCmd).isEqualTo(Command.builder()
        .path(rootPath.toString())
        .environment(ImmutableMap.of())
        .args(ImmutableList.of("git", "config", "--get", "remote.origin.url"))
        .build());
  }

  @Test
  void shouldDisconnect() {
    final var owner = OwnerTest.USER_OWNER;
    final var root = Paths.get("testDir");
    final var git = root.resolve(".git").toFile();
    Assertions.assertThat(git.mkdir()).isTrue();
    given(ownerToPath.apply(owner)).willReturn(root);
    projectService.disconnect(owner).block();
    Assertions.assertThat(git.exists()).isFalse();
  }

}