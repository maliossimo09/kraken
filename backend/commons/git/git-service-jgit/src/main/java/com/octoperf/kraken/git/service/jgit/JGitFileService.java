package com.octoperf.kraken.git.service.jgit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.octoperf.kraken.git.entity.GitFileStatus;
import com.octoperf.kraken.git.entity.GitIdentity;
import com.octoperf.kraken.git.entity.GitLog;
import com.octoperf.kraken.git.entity.GitStatus;
import com.octoperf.kraken.git.entity.command.GitCommand;
import com.octoperf.kraken.git.event.GitRefreshStorageEvent;
import com.octoperf.kraken.git.event.GitStatusUpdateEvent;
import com.octoperf.kraken.git.service.api.GitFileService;
import com.octoperf.kraken.git.service.jgit.command.GitCommandExecutor;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.tools.event.bus.EventBus;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitFileService implements GitFileService, AutoCloseable {

  private static final int MAX_EVENTS_SIZE = 100;
  private static final Duration MAX_EVENTS_TIMEOUT_MS = Duration.ofMillis(5000);

  @NonNull Owner owner;
  @NonNull Git git;
  @NonNull EventBus eventBus;
  @NonNull Map<String, GitCommandExecutor> commandExecutors;

  @Override
  public Mono<Void> execute(final GitCommand command) {
    final var executor = this.commandExecutors.get(command.getClass().getSimpleName());
    return executor.execute(this.git, command)
        .doOnTerminate(() -> {
          eventBus.publish(GitStatusUpdateEvent.builder().owner(owner).build());
          if (executor.refreshStorage()) {
            eventBus.publish(GitRefreshStorageEvent.builder().owner(owner).build());
          }
        });
  }

  @Override
  public Mono<List<GitLog>> log(final String path) {
    return Mono.fromCallable(() -> git.log().addPath(path).setMaxCount(100).call())
        .map(revCommits -> ImmutableList.copyOf(revCommits).stream()
            .map(revCommit -> GitLog.builder()
                .id(ObjectId.toString(revCommit.getId()))
                .message(revCommit.getFullMessage())
                .time(revCommit.getCommitTime())
                .path(path)
                .encoding(revCommit.getEncoding().name())
                .author(GitIdentity.builder().name(revCommit.getAuthorIdent().getName()).email(revCommit.getAuthorIdent().getEmailAddress()).build())
                .committer(GitIdentity.builder().name(revCommit.getCommitterIdent().getName()).email(revCommit.getCommitterIdent().getEmailAddress()).build())
                .build())
            .collect(Collectors.toUnmodifiableList())
        );
  }

  @Override
  public Mono<String> cat(final GitLog log) {
    return Mono.fromCallable(() -> {
      final var repo = git.getRepository();
      final ObjectId objectId = ObjectId.fromString(log.getId());
      try (final ObjectReader reader = repo.newObjectReader()) {
        final var walk = new RevWalk(reader);
        final var commit = walk.parseCommit(objectId);
        final var tree = commit.getTree();
        final var treeWalk = TreeWalk.forPath(reader, log.getPath(), tree);
        if (treeWalk != null) {
          final var data = reader.open(treeWalk.getObjectId(0)).getBytes();
          return new String(data, log.getEncoding());
        } else {
          return "";
        }
      }
    });
  }

  @Override
  public Mono<GitStatus> status() {
    return Mono.fromCallable(() -> git.status().call()).map(status -> {
      final var diff = ImmutableMultimap.<String, GitFileStatus>builder();
      status.getAdded().forEach(path -> diff.put(path, GitFileStatus.ADDED));
      status.getChanged().forEach(path -> diff.put(path, GitFileStatus.CHANGED));
      status.getConflicting().forEach(path -> diff.put(path, GitFileStatus.CONFLICTING));
      status.getIgnoredNotInIndex().forEach(path -> diff.put(path, GitFileStatus.IGNORED_NOT_IN_INDEX));
      status.getMissing().forEach(path -> diff.put(path, GitFileStatus.MISSING));
      status.getModified().forEach(path -> diff.put(path, GitFileStatus.MODIFIED));
      status.getRemoved().forEach(path -> diff.put(path, GitFileStatus.REMOVED));
      status.getUntracked().forEach(path -> diff.put(path, GitFileStatus.UNTRACKED));
      status.getUntrackedFolders().forEach(path -> diff.put(path, GitFileStatus.UNTRACKED));

      final var conflicts = ImmutableMap.<String, String>builder();
      status.getConflictingStageState().forEach((key, value) -> conflicts.put(key, value.name()));

      final var repositoryState = git.getRepository().getRepositoryState();

      return GitStatus.builder()
          .repositoryState(repositoryState.name())
          .repositoryStateDescription(repositoryState.getDescription())
          .diff(diff.build())
          .conflicts(conflicts.build())
          .uncommittedChanges(status.hasUncommittedChanges())
          .clean(status.isClean())
          .build();
    });
  }

  @Override
  public Flux<GitStatus> watchStatus() {
    return this.eventBus.of(GitStatusUpdateEvent.class)
        .filter(event -> event.getOwner().equals(owner))
        .windowTimeout(MAX_EVENTS_SIZE, MAX_EVENTS_TIMEOUT_MS)
        .flatMap(window -> this.status());
  }

  @Override
  public Flux<GitRefreshStorageEvent> watchRefresh() {
    return this.eventBus.of(GitRefreshStorageEvent.class)
        .filter(event -> event.getOwner().equals(owner))
        .windowTimeout(MAX_EVENTS_SIZE, MAX_EVENTS_TIMEOUT_MS)
        .flatMap(window -> window.reduce((event1, event2) -> event2));
  }

  @Override
  public void close() {
    git.close();
  }

}
