package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitSubCommand;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public abstract class GitSubCommandExecutorTest<T extends GitSubCommand, E extends GitCommandExecutor> {

  @Mock
  protected Git git;

  protected T command;
  protected E executor;

  @BeforeEach
  public void setUp() {
    this.command = newCommand();
    this.executor = newCommandExecutor();
  }

  @Test
  void shouldGetCommandClass() {
    assertThat(this.executor.getCommandClass()).isEqualTo(this.command.getClass().getSimpleName());
  }

  @Test
  void shouldRefreshStorage() {
    assertThat(this.executor.refreshStorage()).isEqualTo(this.refreshStorage());
  }

  protected boolean refreshStorage() {
    return false;
  }

  protected abstract void shouldExecute() throws Exception;

  protected abstract T newCommand();

  protected abstract E newCommandExecutor();

}