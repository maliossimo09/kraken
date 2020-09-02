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
final class JGitFileService implements GitFileService, AutoCloseable {

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

  @Override
  public void close() {
    git.close();
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

  // https://stackoverflow.com/questions/28073266/how-to-use-jgit-to-push-changes-to-remote-with-oauth-access-token
  // https://github.com/centic9/jgit-cookbook
  // https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/CloneRemoteRepositoryWithAuthentication.java
  // https://stackoverflow.com/questions/23692747/specifying-ssh-key-for-jgit
  // https://medium.com/keycloak/github-as-identity-provider-in-keyclaok-dca95a9d80ca
  // https://stackoverflow.com/questions/28380719/how-to-use-jgit-to-clone-the-existing-repositories-to-new-github-instance
  // https://docs.cachethq.io/docs/github-oauth-token#:~:text=Generate%20a%20new%20token,list%20of%20tokens%20from%20before.

  // TODO initialize repository => created on the server, create all files (existing local source)

  // TODO Handle merge conflicts
  //  front can update files?
  //  how to commit them once updated?
  //  https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/resolving-a-merge-conflict-using-the-command-line

//  final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
//    @Override
//    protected JSch createDefaultJSch(FS fs) throws JSchException {
//      final var jsch = super.createDefaultJSch(fs);
//      jsch.addIdentity("/home/ubuntu/kraken/id_rsa");
//      return jsch;
//    }
//  };
//
//  final TransportConfigCallback transportConfigCallback = transport -> {
//    final SshTransport sshTransport = (SshTransport) transport;
//    sshTransport.setSshSessionFactory(sshSessionFactory);
//  };
//
//  @Test
//  void jsch() throws Exception {
//    final var filename = "/home/ubuntu/kraken/id_rsa";
//    final var comment = "comment";
//    JSch jsch = new JSch();
//    com.jcraft.jsch.KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
//    kpair.writePrivateKey(filename);
//    kpair.writePublicKey(filename + ".pub", comment);
//    System.out.println("Finger print: " + kpair.getFingerPrint());
//    kpair.dispose();
//  }
//
//  @Test
//  void cloneRepo() throws Exception {
//    final CloneCommand command = new CloneCommand();
//    command.setURI("git@github.com:geraldpereira/gatlingTest.git")
//        .setDirectory(new File("testDir/gatling"))
//        .setTransportConfigCallback(transportConfigCallback);
//    command.call();
//  }
//
//  @Test
//  void fetch() throws Exception {
//    Git git = Git.open(new File("testDir/gatling"));
//    System.out.println(git.fetch().setTransportConfigCallback(transportConfigCallback).call());
//  }
//
//  @Test
//  void pull() throws Exception {
//    Git git = Git.open(new File("testDir/gatling"));
//    System.out.println(git.pull().setTransportConfigCallback(transportConfigCallback).call());
//  }
//
//  @Test
//  void push() throws Exception {
//    Git git = Git.open(new File("testDir/gatling"));
//    System.out.println(git.push().setTransportConfigCallback(transportConfigCallback).call());
//  }
//
//  @Test
//  void commit() throws Exception {
//    Git git = Git.open(new File("testDir/gatling"));
//    System.out.println(git.commit().setMessage("Kraken").call());
//  }
//
//  @Test
//  void addAll() throws Exception {
//    Git git = Git.open(new File("testDir/gatling"));
//    git.add().addFilepattern(".").call();
//  }
//
//  @Test
//  void rebase() throws Exception {
//    Git git = Git.open(new File("testDir/gatling"));
//    git.rebase().setOperation(RebaseCommand.Operation.CONTINUE).call();
//
//    // https://stackoverflow.com/questions/36372274/how-to-get-conflicts-before-merge-with-jgit
//  }
//
//  @Test
//  void status() throws Exception {
//    Git git = Git.open(new File("testDir/gatling"));
//    final var status = git.status().call();
//    System.out.println(status.getIgnoredNotInIndex());
//    System.out.println(status.getAdded());
//    System.out.println(status.getConflicting());
//    System.out.println(status.getUntracked());
//    System.out.println(status.getUntrackedFolders());
//    System.out.println(status.getChanged());
//    System.out.println(status.getMissing());
//    System.out.println(status.getUncommittedChanges());
//    System.out.println(status.hasUncommittedChanges());
//    System.out.println(status.getRemoved());
//    System.out.println(status.isClean());
//
//
//    // SYNC:
//    // 'add '.'
//    // 'commit with a message
//    // 'pull
//    // 'status
//    // If conflicts => ask to resolve
//    // Mark as resolved => 'add the specified file
//    // 'rebase
//    // 'push
//  }

}
