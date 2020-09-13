package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.Application;
import com.octoperf.kraken.config.api.ApplicationProperties;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.security.entity.owner.OwnerType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.BDDMockito.given;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Tag("integration")
public class JGitSubCommandLineServiceIntegrationTest {

  private static final String USER_ID = "userId";
  private static final String APP_ID = "app";
  private static final String PROJECT_ID = "project";
  private static final Owner OWNER = Owner.builder()
      .userId(USER_ID)
      .projectId(PROJECT_ID)
      .applicationId(APP_ID)
      .type(OwnerType.USER)
      .build();
  public static final String REPO_URL = "git@github.com:geraldpereira/gatlingTest.git";

  @Autowired
  JGitCommandLineService gitCommandLineService;

  @MockBean
  ApplicationProperties properties;

  @MockBean
  UserProvider userProvider;

  Path projectPath;
  Path repoPath;

  @BeforeEach
  public void before() {
    given(properties.getData()).willReturn("/home/ubuntu/kraken/gitTest/");
    projectPath = Paths.get(properties.getData(), "users", USER_ID, PROJECT_ID);
    repoPath = projectPath.resolve(APP_ID);
    final var projectFile = projectPath.toFile();
    Assertions.assertThat(projectFile.exists() || repoPath.toFile().mkdirs()).isTrue();
  }

  @Test
  void shouldExecuteCommand() throws Exception {
    // TODO faire en sorte que le TextBuiltin ne surcharge pas notre SSHmachin ...
    gitCommandLineService.execute(OWNER, "status --ssh JSCH ");
  }

}
