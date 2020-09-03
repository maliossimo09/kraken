package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.security.entity.owner.OwnerTest;
import org.assertj.core.api.Assertions;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JGitProjectServiceTest {

  @Mock
  OwnerToTransportConfig ownerToTransportConfig;
  @Mock
  TransportConfigCallback transportConfigCallback;
  @Mock
  OwnerToPath ownerToPath;
  @Mock
  Supplier<CloneCommand> commandSupplier;
  @Mock
  Supplier<FileRepositoryBuilder> repositoryBuilderSupplier;
  @Mock
  CloneCommand cloneCommand;
  @Mock
  FileRepositoryBuilder fileRepositoryBuilder;
  @Mock
  FileRepository fileRepository;
  @Mock
  FileBasedConfig fileBasedConfig;

  JGitProjectService projectService;

  @BeforeEach
  public void beforeEach() {
    projectService = new JGitProjectService(ownerToTransportConfig,
        ownerToPath,
        commandSupplier,
        repositoryBuilderSupplier);
  }

  @Test
  void shouldConnect() throws IOException {
    final var owner = OwnerTest.USER_OWNER;
    final var repoUrl = "repoUrl";
    given(ownerToTransportConfig.apply(owner)).willReturn(Mono.just(transportConfigCallback));
    given(ownerToPath.apply(owner)).willReturn(Paths.get("testDir"));
    given(commandSupplier.get()).willReturn(cloneCommand);
    given(cloneCommand.setURI(repoUrl)).willReturn(cloneCommand);
    given(cloneCommand.setDirectory(any())).willReturn(cloneCommand);
    given(cloneCommand.setTransportConfigCallback(transportConfigCallback)).willReturn(cloneCommand);
    given(repositoryBuilderSupplier.get()).willReturn(fileRepositoryBuilder);
    given(fileRepositoryBuilder.build()).willReturn(fileRepository);
    given(fileRepository.getConfig()).willReturn(fileBasedConfig);
    given(fileBasedConfig.getString("remote", "origin", "url")).willReturn(repoUrl);
    final var config = projectService.connect(owner, repoUrl).block();
    Assertions.assertThat(config)
        .isNotNull()
        .isEqualTo(GitConfiguration.builder().repositoryUrl(repoUrl).build());

  }

  @Test
  void shouldDisconnect() throws IOException {
    final var owner = OwnerTest.USER_OWNER;
    final var root = Paths.get("testDir");
    final var git = root.resolve(".git").toFile();
    Assertions.assertThat(git.mkdir()).isTrue();
    given(ownerToPath.apply(owner)).willReturn(root);
    projectService.disconnect(owner).block();
    Assertions.assertThat(git.exists()).isFalse();
  }

}