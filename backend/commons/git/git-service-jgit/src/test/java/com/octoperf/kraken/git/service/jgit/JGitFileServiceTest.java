package com.octoperf.kraken.git.service.jgit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.octoperf.kraken.git.entity.GitFileStatus;
import com.octoperf.kraken.git.entity.GitStatus;
import com.octoperf.kraken.git.entity.command.GitCommand;
import com.octoperf.kraken.git.event.GitRefreshStorageEvent;
import com.octoperf.kraken.git.event.GitStatusUpdateEvent;
import com.octoperf.kraken.git.service.jgit.command.GitCommandExecutor;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.security.entity.owner.OwnerTest;
import com.octoperf.kraken.tools.event.bus.EventBus;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JGitFileServiceTest {

  private static final Owner OWNER = OwnerTest.USER_OWNER;

  @Mock
  Git git;
  @Mock
  EventBus eventBus;
  @Mock
  GitCommandExecutor commandExecutor;
  @Mock
  GitCommand command;
  @Mock
  StatusCommand statusCommand;
  @Mock
  Status status;
  @Mock
  Repository repository;

  JGitFileService service;

  @BeforeEach
  void setUp() {
    given(commandExecutor.getCommandClass()).willReturn(command.getClass().getSimpleName());
    service = new JGitFileService(OWNER,
        git,
        eventBus,
        ImmutableMap.of(commandExecutor.getCommandClass(), commandExecutor));
  }

  @Test
  void shouldExecute() {
    given(commandExecutor.refreshStorage()).willReturn(false);
    given(commandExecutor.execute(git, command)).willReturn(Mono.empty());
    service.execute(command).block();
    verify(eventBus).publish(GitStatusUpdateEvent.builder().owner(OWNER).build());
    verify(eventBus, never()).publish(GitRefreshStorageEvent.builder().owner(OWNER).build());
  }

  @Test
  void shouldExecuteRefresh() {
    given(commandExecutor.refreshStorage()).willReturn(true);
    given(commandExecutor.execute(git, command)).willReturn(Mono.empty());
    service.execute(command).block();
    verify(eventBus).publish(GitStatusUpdateEvent.builder().owner(OWNER).build());
    verify(eventBus).publish(GitRefreshStorageEvent.builder().owner(OWNER).build());
  }

  @Test
  void shouldReturnStatus() throws Exception {
    mockStatus();
    final var diff = ImmutableMultimap.<String, GitFileStatus>builder();
    diff.put("added", GitFileStatus.ADDED);
    diff.put("changed", GitFileStatus.CHANGED);
    diff.put("conflicting", GitFileStatus.CONFLICTING);
    diff.put("ignored", GitFileStatus.IGNORED_NOT_IN_INDEX);
    diff.put("missing", GitFileStatus.MISSING);
    diff.put("modified", GitFileStatus.MODIFIED);
    diff.put("removed", GitFileStatus.REMOVED);
    diff.put("untracked", GitFileStatus.UNTRACKED);
    diff.put("untracked-folder", GitFileStatus.UNTRACKED);

    final var status = service.status().block();
    assertThat(status).isNotNull()
        .isEqualTo(GitStatus.builder()
            .diff(diff.build())
            .clean(false)
            .uncommittedChanges(true)
            .conflicts(ImmutableMap.of("conflict", "BOTH_ADDED"))
            .repositoryState("APPLY")
            .repositoryStateDescription("Apply mailbox")
            .build());
  }

  private void mockStatus() throws Exception {
    given(git.status()).willReturn(statusCommand);
    given(statusCommand.call()).willReturn(status);
    given(status.getAdded()).willReturn(ImmutableSet.of("added"));
    given(status.getChanged()).willReturn(ImmutableSet.of("changed"));
    given(status.getConflicting()).willReturn(ImmutableSet.of("conflicting"));
    given(status.getIgnoredNotInIndex()).willReturn(ImmutableSet.of("ignored"));
    given(status.getMissing()).willReturn(ImmutableSet.of("missing"));
    given(status.getModified()).willReturn(ImmutableSet.of("modified"));
    given(status.getRemoved()).willReturn(ImmutableSet.of("removed"));
    given(status.getUntracked()).willReturn(ImmutableSet.of("untracked"));
    given(status.getUntrackedFolders()).willReturn(ImmutableSet.of("untracked-folder"));
    given(status.getConflictingStageState()).willReturn(ImmutableMap.of("conflict", IndexDiff.StageState.BOTH_ADDED));
    given(status.isClean()).willReturn(false);
    given(status.hasUncommittedChanges()).willReturn(true);
    given(git.getRepository()).willReturn(repository);
    given(repository.getRepositoryState()).willReturn(RepositoryState.APPLY);
  }

  @Test
  void shouldWatchStatus() throws Exception {
    mockStatus();
    given(eventBus.of(GitStatusUpdateEvent.class)).willReturn(Flux.range(0, 299).map(integer -> GitStatusUpdateEvent.builder().owner(OWNER).build()));
    final var statuses = service.watchStatus().collectList().block();
    assertThat(statuses).isNotNull().hasSize(3);
  }

  @Test
  void shouldWatchRefresh() {
    given(eventBus.of(GitRefreshStorageEvent.class)).willReturn(Flux.range(0, 299).map(integer -> GitRefreshStorageEvent.builder().owner(OWNER).build()));
    final var refreshes = service.watchRefresh().collectList().block();
    assertThat(refreshes).isNotNull().hasSize(3);
  }

  @Test
  void shouldClose() {
    service.close();
    verify(git).close();
  }
}