package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.*;
import org.eclipse.jgit.api.RmCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class GitRmCommandExecutorTest extends GitCommandExecutorTest<GitRmCommand, GitRmCommandExecutor> {

  @Mock
  RmCommand gitCommand;

  @Test
  @Override
  protected void shouldExecute() throws Exception {
    given(git.rm()).willReturn(gitCommand);
    executor.execute(git, command).block();
    verify(gitCommand).addFilepattern(command.getFilePatterns().get(0));
    verify(gitCommand).setCached(command.getCached().orElseThrow());
    verify(gitCommand).call();
  }

  @Override
  protected GitRmCommand newCommand() {
    return GitRmCommandTest.COMMAND;
  }

  @Override
  protected GitRmCommandExecutor newCommandExecutor() {
    return new GitRmCommandExecutor();
  }

  @Override
  protected boolean refreshStorage() {
    return true;
  }
}
