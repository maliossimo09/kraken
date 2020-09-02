package com.octoperf.kraken.git.service.jgit;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.octoperf.kraken.Application;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.util.FS;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Tag("integration")
public class GitIntegrationTest {



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

  final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
    @Override
    protected JSch createDefaultJSch(FS fs) throws JSchException {
      final var jsch = super.createDefaultJSch(fs);
      jsch.addIdentity("/home/ubuntu/kraken/id_rsa");
      return jsch;
    }
  };

  final TransportConfigCallback transportConfigCallback = transport -> {
    final SshTransport sshTransport = (SshTransport) transport;
    sshTransport.setSshSessionFactory(sshSessionFactory);
  };

  @Test
  void jsch() throws Exception {
    final var filename = "/home/ubuntu/kraken/id_rsa";
    final var comment = "comment";
    JSch jsch = new JSch();
    com.jcraft.jsch.KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
    kpair.writePrivateKey(filename);
    kpair.writePublicKey(filename + ".pub", comment);
    System.out.println("Finger print: " + kpair.getFingerPrint());
    kpair.dispose();
  }

  @Test
  void cloneRepo() throws Exception {
    final CloneCommand command = new CloneCommand();
    command.setURI("git@github.com:geraldpereira/gatlingTest.git")
        .setDirectory(new File("testDir/gatling"))
        .setTransportConfigCallback(transportConfigCallback);
    command.call();
  }

  @Test
  void fetch() throws Exception {
    Git git = Git.open(new File("testDir/gatling"));
    System.out.println(git.fetch().setTransportConfigCallback(transportConfigCallback).call());
  }

  @Test
  void pull() throws Exception {
    Git git = Git.open(new File("testDir/gatling"));
    System.out.println(git.pull().setTransportConfigCallback(transportConfigCallback).call());
  }

  @Test
  void push() throws Exception {
    Git git = Git.open(new File("testDir/gatling"));
    System.out.println(git.push().setTransportConfigCallback(transportConfigCallback).call());
  }

  @Test
  void commit() throws Exception {
    Git git = Git.open(new File("testDir/gatling"));
    System.out.println(git.commit().setMessage("Kraken").call());
  }

  @Test
  void addAll() throws Exception {
    Git git = Git.open(new File("testDir/gatling"));
    git.add().addFilepattern(".").call();
  }

  @Test
  void rebase() throws Exception {
    Git git = Git.open(new File("testDir/gatling"));
    git.rebase().setOperation(RebaseCommand.Operation.CONTINUE).call();

    // https://stackoverflow.com/questions/36372274/how-to-get-conflicts-before-merge-with-jgit
  }

  @Test
  void status() throws Exception {
    Git git = Git.open(new File("testDir/gatling"));
    final var status = git.status().call();
    System.out.println(status.getIgnoredNotInIndex());
    System.out.println(status.getAdded());
    System.out.println(status.getConflicting());
    System.out.println(status.getUntracked());
    System.out.println(status.getUntrackedFolders());
    System.out.println(status.getChanged());
    System.out.println(status.getMissing());
    System.out.println(status.getUncommittedChanges());
    System.out.println(status.hasUncommittedChanges());
    System.out.println(status.getRemoved());
    System.out.println(status.isClean());


    // SYNC:
    // 'add '.'
    // 'commit with a message
    // 'pull
    // 'status
    // If conflicts => ask to resolve
    // Mark as resolved => 'add the specified file
    // 'rebase
    // 'push
  }
}
