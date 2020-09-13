package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitAddSubCommand;
import com.octoperf.kraken.git.entity.command.GitAddSubCommandTest;
import org.eclipse.jgit.api.AddCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class GitAddCommandExecutorTest extends GitSubCommandExecutorTest<GitAddSubCommand, GitAddCommandExecutor> {

  @Mock
  AddCommand gitCommand;

  @Test
  @Override
  protected void shouldExecute() throws Exception{
    given(git.add()).willReturn(gitCommand);
    executor.execute(git,  command).block();
    verify(gitCommand).addFilepattern(command.getFilePatterns().get(0));
    verify(gitCommand).setUpdate(command.getUpdate().orElseThrow());
    verify(gitCommand).call();
  }

  @Override
  protected GitAddSubCommand newCommand() {
    return GitAddSubCommandTest.COMMAND;
  }

  @Override
  protected GitAddCommandExecutor newCommandExecutor() {
    return new GitAddCommandExecutor();
  }

}
