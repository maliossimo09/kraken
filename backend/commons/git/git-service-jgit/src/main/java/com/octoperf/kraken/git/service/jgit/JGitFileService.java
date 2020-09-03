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
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitFileService implements GitFileService, AutoCloseable {

  private static final int MAX_EVENTS_SIZE = 500;
  private static final Duration MAX_EVENTS_TIMEOUT_MS = Duration.ofMillis(5000);

  @NonNull Owner owner;
  @NonNull Path root;
  @NonNull Git git;
  @NonNull TransportConfigCallback transportConfigCallback;
  @NonNull StorageClient storageClient;
  @NonNull EventBus eventBus;

  // TODO GitEvent owner: Owner, kind: 'REFRESH' | 'STATUS', GitStatusUpdateEvent | GitRefreshEvent (triggered when the files need to be updated)
  //  Deux types d'events séparés sans container ce sera plus propre, le SSEController appellera les deux
  // TODO Listener du storage en mode admin qui ecoute tous les events et re-dispatch ensuite des GitStatusUpdateEvent a la volée
  // TODO Toutes les opérations possible avec tous leurs paramètres
  //  Créer des objects pour chaque commande puis des CommandExecutors

  // TODO Automatically call add/remove by listening to the storage?
  public Mono<Void> add(final Optional<String> pattern) {
    return Mono.fromCallable(() -> git.add().addFilepattern(pattern.orElse(".")).call())
        .doFinally(signalType -> eventBus.publish(new GitStatusUpdateEvent()))
        .then();
  }
  public Mono<Void> remove(final String path) {
    return Mono.fromCallable(() -> git.rm().addFilepattern(path).call())
        .doFinally(signalType -> eventBus.publish(new GitStatusUpdateEvent()))
        .then();
  }

  public Mono<GitStatus> status() {
    return Mono.fromCallable(() -> git.status().call()).map(status -> {

      // TODO Include this in the status
      System.out.println(git.getRepository().getRepositoryState());

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
    // TODO add the current user to the GitStatusUpdateEvent and check that it matches
    final var gitEvents = this.eventBus.of(GitStatusUpdateEvent.class);
    // TODO make the central storage watcher dispatch GitStatusUpdateEvents
    final var storageEvents = storageClient.watch();
    return Flux.merge(gitEvents, storageEvents)
        .windowTimeout(MAX_EVENTS_SIZE, MAX_EVENTS_TIMEOUT_MS)
        .flatMap(busEventFlux -> this.status());
  }

  @Override
  public void close() {
    git.close();
  }

  // TODO Set author from connected user
  public Mono<Void> commit(final String message) {
    return Mono.fromCallable(() -> git.commit()..setMessage(message).call())
        .doFinally(signalType -> eventBus.publish(new GitStatusUpdateEvent()))
        .then();
  }

  public Mono<Void> fetch() {
    return Mono.fromCallable(() -> git.fetch().setTransportConfigCallback(transportConfigCallback).call())
        .doFinally(signalType -> eventBus.publish(new GitStatusUpdateEvent()))
        .then();
  }

  public Mono<Void> pull() {
    return Mono.fromCallable(() -> git.pull().setTransportConfigCallback(transportConfigCallback).call())
        .doFinally(signalType -> eventBus.publish(new GitStatusUpdateEvent()))
        .then();
  }

  public Mono<Void> push() {
    return Mono.fromCallable(() -> git.push().setTransportConfigCallback(transportConfigCallback).call())
        .doFinally(signalType -> eventBus.publish(new GitStatusUpdateEvent()))
        .then();
  }

  public Mono<Void> merge() {
    return Mono.fromCallable(() -> git.merge().call())
        .doFinally(signalType -> eventBus.publish(new GitStatusUpdateEvent()))
        .then();
  }

  public Mono<Void> rebase(final String operation) {
    // https://stackoverflow.com/questions/36372274/how-to-get-conflicts-before-merge-with-jgit
    return Mono.fromCallable(() -> git.rebase().setOperation(RebaseCommand.Operation.valueOf(operation)).call())
        .doFinally(signalType -> eventBus.publish(new GitStatusUpdateEvent()))
        .then();
  }

  public Flux<String> log(final String path) {
    // https://stackoverflow.com/questions/36372274/how-to-get-conflicts-before-merge-with-jgit
    return Mono.fromCallable(() -> git.log().addPath(path).setMaxCount(100).call())
        .flatMapMany(Flux::fromIterable)
        // TODO Create GitLog object
        .map(revCommit -> ObjectId.toString(revCommit.getId()) + "-" + revCommit.getFullMessage() + " Enc: " + revCommit.getEncoding() + "<=>" + revCommit.getCommitterIdent().getEmailAddress());
  }


  public Mono<String> cat(final String id, final String path) {
    return Mono.fromCallable(() -> {
      final var repo = git.getRepository();

      // Resolve the revision specification
      final ObjectId objectId = ObjectId.fromString(id);

      // Makes it simpler to release the allocated resources in one go
      try (final ObjectReader reader = repo.newObjectReader()) {
        // Get the commit object for that revision
        RevWalk walk = new RevWalk(reader);
        RevCommit commit = walk.parseCommit(objectId);

        // Get the revision's file tree
        RevTree tree = commit.getTree();
        // .. and narrow it down to the single file's path
        TreeWalk treewalk = TreeWalk.forPath(reader, path, tree);

        if (treewalk != null) {
          // use the blob id to read the file's data
          byte[] data = reader.open(treewalk.getObjectId(0)).getBytes();
          return new String(data, StandardCharsets.UTF_8);
        } else {
          return "";
        }
      }
    });
  }

  // TODO Reset to head (file or whole repository)
  //  git reset --hard HEAD

  // TODO keepTheirs

  // TODO keepOurs

  // TODO Juste methode sync
  // Depend du status du repo
  // Status pour commencer
  // Si conflits retourne
  // add all UNTRACKED files
  // Remove all MISSING files
  // Commit with a message
  // Pull
  // Status pour check les conflits
  // Si conflits retourne
  // Utilisateur resoud les conflits soit a la main soit ... keep theirs / keep ours

  // SYNC:
  // 'add '.'
  // 'commit with a message
  // 'pull

  // status


}
