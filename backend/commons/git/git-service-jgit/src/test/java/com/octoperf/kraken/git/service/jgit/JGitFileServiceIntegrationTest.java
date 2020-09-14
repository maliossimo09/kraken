package com.octoperf.kraken.git.service.jgit;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.Application;
import com.octoperf.kraken.config.api.ApplicationProperties;
import com.octoperf.kraken.git.entity.GitFileStatus;
import com.octoperf.kraken.git.entity.GitStatus;
import com.octoperf.kraken.git.command.GitAddSubCommand;
import com.octoperf.kraken.git.command.GitCommitSubCommand;
import com.octoperf.kraken.security.authentication.api.AuthenticationMode;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import com.octoperf.kraken.security.authentication.api.UserProviderFactory;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.security.entity.owner.OwnerTest;
import com.octoperf.kraken.security.entity.token.KrakenTokenUserTest;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class JGitFileServiceIntegrationTest {

  public static final Owner OWNER = OwnerTest.USER_OWNER;
  private static final Path ROOT = Paths.get("testDir", "git");
  private static final Path REPO_PATH = ROOT.resolve(Paths.get("users",
      OWNER.getUserId(),
      OWNER.getProjectId(),
      OWNER.getApplicationId()));

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
  public void before() throws Exception {
    given(properties.getData()).willReturn(ROOT.toString());
    given(userProviderFactory.getMode()).willReturn(AuthenticationMode.SESSION);
    given(userProviderFactory.create("")).willReturn(userProvider);
    given(userProvider.getOwner(any(), any())).willReturn(Mono.just(OWNER));
    given(userProvider.getAuthenticatedUser()).willReturn(Mono.just(KrakenTokenUserTest.KRAKEN_USER));

    assertThat(REPO_PATH.toFile().mkdirs()).isTrue();
    Git.init().setDirectory(REPO_PATH.toFile()).call();

    gitFileService = (JGitFileService) gitFileServiceBuilder.build(OWNER).block();
  }

  @AfterEach
  public void after() throws Exception {
    gitFileService.close();
    FileSystemUtils.deleteRecursively(ROOT);
  }

  private GitStatus getStatus() {
    System.out.println("============================");
    final var status = gitFileService.status().block();
    assertThat(status).isNotNull();
    System.out.println(status);
    return status;
  }

  @Test
  void shouldLExecuteAndLog() throws Exception {
    final var fileName = "README.md";
    getStatus();

    System.out.println("Create a file");
    Files.writeString(REPO_PATH.resolve(fileName), "content");
    assertThat(getStatus().getDiff().get(fileName)).isEqualTo(ImmutableList.of(GitFileStatus.UNTRACKED));

    System.out.println("Add it to git");
    gitFileService.execute(GitAddSubCommand.builder()
        .filePatterns(ImmutableList.of(fileName))
//        .update(Optional.empty())
        .build()).block();
    assertThat(getStatus().getDiff().get(fileName)).isEqualTo(ImmutableList.of(GitFileStatus.ADDED));

    System.out.println("Commit");
    gitFileService.execute(GitCommitSubCommand.builder()
        .message("Commit")
//        .all(Optional.of(true))
//        .allowEmpty(Optional.empty())
//        .amend(Optional.empty())
//        .noVerify(Optional.empty())
//        .only(ImmutableList.of())
        .build()).block();
    assertThat(getStatus().isUncommittedChanges()).isFalse();

    final var logs = gitFileService.log(fileName).block();
    System.out.println(logs);

    final var cat = gitFileService.cat(logs.get(0)).block();
    assertThat(cat).isEqualTo("content");
  }

}
