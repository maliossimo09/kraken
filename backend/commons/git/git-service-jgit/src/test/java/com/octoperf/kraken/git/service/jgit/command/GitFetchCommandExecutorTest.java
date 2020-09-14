package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.command.GitFetchSubCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class GitFetchCommandExecutorTest extends GitSubCommandExecutorTest<GitFetchSubCommand, GitFetchCommandExecutor> {

  @Mock
  FetchCommand gitCommand;

  @Test
  @Override
  protected void shouldExecute() throws Exception{
    given(git.fetch()).willReturn(gitCommand);
    executor.execute(git, command).block();
//    verify(gitCommand).setRemote(command.getRemote().orElseThrow());
//    verify(gitCommand).setForceUpdate(command.getForceUpdate().orElseThrow());
    verify(gitCommand, never()).setDryRun(anyBoolean());
    verify(gitCommand).call();
  }

  @Override
  protected GitFetchSubCommand newCommand() {
    return null;
//    return GitFetchCommandTest.COMMAND;
  }

  @Override
  protected GitFetchCommandExecutor newCommandExecutor() {
    return new GitFetchCommandExecutor();
  }

}
