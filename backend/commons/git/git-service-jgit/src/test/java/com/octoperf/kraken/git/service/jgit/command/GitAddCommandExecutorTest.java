package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitAddCommand;
import com.octoperf.kraken.git.entity.command.GitAddCommandTest;
import org.eclipse.jgit.api.AddCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class GitAddCommandExecutorTest extends GitCommandExecutorTest<GitAddCommand, GitAddCommandExecutor> {

  @Mock
  AddCommand gitCommand;

  @Test
  @Override
  protected void shouldExecute() throws Exception{
    given(git.add()).willReturn(gitCommand);
    executor.execute(git, transportConfigCallback, command).block();
    verify(gitCommand).addFilepattern(command.getFilePatterns().get(0));
    verify(gitCommand).setUpdate(command.getUpdate().orElseThrow());
    verify(gitCommand).call();
  }

  @Override
  protected GitAddCommand newCommand() {
    return GitAddCommandTest.COMMAND;
  }

  @Override
  protected GitAddCommandExecutor newCommandExecutor() {
    return new GitAddCommandExecutor();
  }

}
