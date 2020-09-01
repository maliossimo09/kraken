package com.octoperf.kraken.git.service.jgit;

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
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitFileService implements GitFileService {

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

  public Mono<Void> resolve(final String path) {
    return this.add(Optional.of(path));
  }

  public Mono<GitStatus> status() {
    // TODO Multi valued set <Path, GitFileStatus[]>
//    uncommittedChanges.addAll(diff.getAdded());
//    uncommittedChanges.addAll(diff.getChanged());
//    uncommittedChanges.addAll(diff.getRemoved());
//    uncommittedChanges.addAll(diff.getMissing());
//    uncommittedChanges.addAll(diff.getModified());
//    uncommittedChanges.addAll(diff.getConflicting());

//    System.out.println(status.getConflicting());
//    System.out.println(status.getUntracked());
//    System.out.println(status.getUntrackedFolders());
//    System.out.println(status.getChanged());
//    System.out.println(status.getMissing());
//    System.out.println(status.getUncommittedChanges());
//    System.out.println(status.hasUncommittedChanges());
//    System.out.println(status.getRemoved());
//    System.out.println(status.isClean());
    return Mono.fromCallable(() -> git.status().call()).map(status -> GitStatus.builder()
//        .ignoredNotInIndex(status.getIgnoredNotInIndex())
//        .added(status.getAdded())
//        .untracked(status.getUntracked())
//        .untrackedFolders(status.getUntrackedFolders())
//        .conflicting(status.getConflicting())
//        .changed(status.getChanged())
//        .missing(status.getMissing())
//        .uncommittedChanges(status.getUncommittedChanges())
//        .removed(status.getRemoved())
        // TODO
        .diff(null)
        .hasUncommittedChanges(status.hasUncommittedChanges())
        .clean(status.isClean())
        .build());
  }

  // startSync => status error si conflicts

  // endSync => error si toujours des soucis

  // SYNC:
  // 'add '.'
  // 'commit with a message
  // 'pull

  // status
  // Ecouter les events storage + les events git => mettre a jour si il y'a des modifications

  // listVersions filePath

  // getFileContent filePath version

  public void close() {
    git.close();
  }
}
