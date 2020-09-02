package com.octoperf.kraken.git.service.jgit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.octoperf.kraken.git.entity.GitFileStatus;
import com.octoperf.kraken.git.entity.GitStatus;
import com.octoperf.kraken.git.event.GitStatusUpdateEvent;
import com.octoperf.kraken.git.service.api.GitFileService;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.storage.client.api.StorageClient;
import com.octoperf.kraken.tools.event.bus.EventBus;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitFileService implements GitFileService {

  private static final int MAX_EVENTS_SIZE = 500;
  private static final Duration MAX_EVENTS_TIMEOUT_MS = Duration.ofMillis(5000);

  @NonNull Owner owner;
  @NonNull Path root;
  @NonNull Git git;
  @NonNull TransportConfigCallback transportConfigCallback;
  @NonNull StorageClient storageClient;
  @NonNull EventBus eventBus;

  public Mono<Void> add(final Optional<String> pattern) {
    return Mono.fromCallable(() -> git.add().addFilepattern(pattern.orElse(".")).call())
        .doFinally(signalType -> eventBus.publish(new GitStatusUpdateEvent()))
        .then();
  }

  public Mono<Void> markAsResolved(final String path) {
    return this.add(Optional.of(path));
  }

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
      status.getUntrackedFolders().forEach(path -> diff.put(path, GitFileStatus.CHANGED));

      final var conflicts = ImmutableMap.<String, String>builder();
      status.getConflictingStageState().forEach((key, value) -> conflicts.put(key, value.name()));

      return GitStatus.builder()
          .diff(diff.build())
          .conflicts(conflicts.build())
          .hasUncommittedChanges(status.hasUncommittedChanges())
          .clean(status.isClean())
          .build();
    });
  }

  public Flux<GitStatus> watchStatus() {
    final var gitEvents = this.eventBus.of(GitStatusUpdateEvent.class);
    final var storageEvents = storageClient.watch();
    return Flux.merge(gitEvents, storageEvents)
        .windowTimeout(MAX_EVENTS_SIZE,MAX_EVENTS_TIMEOUT_MS )
        .flatMap(busEventFlux -> this.status());
  }

  // TODO keepTheirs

  // TODO keepOurs

  // TODO startSync => status error si conflicts

  // TODO endSync => error si toujours des soucis

  // SYNC:
  // 'add '.'
  // 'commit with a message
  // 'pull

  // status
  // Ecouter les events storage + les events git => mettre a jour si il y'a des modifications

  // TODO listVersions filePath

  // TODO getFileContent filePath version

  public void close() {
    git.close();
  }
}
