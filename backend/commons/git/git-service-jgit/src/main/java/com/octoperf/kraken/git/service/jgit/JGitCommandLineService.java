package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.git.service.jgit.command.GitCommandExecutor;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.tools.event.bus.EventBus;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.pgm.Main;
import reactor.core.publisher.Mono;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
//@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitCommandLineService extends Main {

  @NonNull OwnerToPath ownerToPath;
  @NonNull Git git;
  @NonNull TransportConfigCallback transportConfigCallback;
  @NonNull EventBus eventBus;
  @NonNull Map<String, GitCommandExecutor> commandExecutors;

  public Mono<Void> execute(final Owner owner, final String command) {
    final var path = this.ownerToPath.apply(owner);

//    SshSessionFactory.setInstance(factory);

    // TODO Force git-dir to path
    // TODO Set the transportConfigCallback at a higher level
    return null;
  }

  // TODO Override this
//  void init(TextBuiltin cmd) throws IOException {
//    if (cmd.requiresRepository()) {
//      cmd.init(openGitDir(gitdir), null);
//    } else {
//      cmd.init(null, gitdir);
//    }
//  }

}
