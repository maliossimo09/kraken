package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitCommitSubCommand;
import com.octoperf.kraken.git.entity.command.GitCommitCommandTest;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import org.eclipse.jgit.api.CommitCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Mono;

import static com.octoperf.kraken.security.entity.token.KrakenTokenUserTest.KRAKEN_USER;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class GitCommitCommandExecutorTest extends GitSubCommandExecutorTest<GitCommitSubCommand, GitCommitCommandExecutor> {

  @Mock
  CommitCommand gitCommand;

  @Mock
  UserProvider userProvider;

  @Test
  @Override
  protected void shouldExecute() throws Exception {
    given(userProvider.getAuthenticatedUser()).willReturn(Mono.just(KRAKEN_USER));
    given(git.commit()).willReturn(gitCommand);
    executor.execute(git, command).block();
    verify(gitCommand).setMessage(command.getMessage());
    verify(gitCommand).setCommitter(KRAKEN_USER.getUsername(), KRAKEN_USER.getEmail());
    verify(gitCommand).setAuthor(KRAKEN_USER.getUsername(), KRAKEN_USER.getEmail());
    verify(gitCommand).setAll(command.getAll().orElseThrow());
    verify(gitCommand, never()).setAmend(anyBoolean());
    verify(gitCommand, never()).setAllowEmpty(anyBoolean());
    verify(gitCommand, never()).setNoVerify(anyBoolean());
    verify(gitCommand).setOnly(command.getOnly().get(0));
    verify(gitCommand).call();
  }

  @Override
  protected GitCommitSubCommand newCommand() {
    return GitCommitCommandTest.COMMAND;
  }

  @Override
  protected GitCommitCommandExecutor newCommandExecutor() {
    return new GitCommitCommandExecutor(userProvider);
  }

}
