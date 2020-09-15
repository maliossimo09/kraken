package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.command.GitSubCommand;
import com.octoperf.kraken.git.command.GitCommitSubCommand;
import com.octoperf.kraken.git.service.api.GitLogsService;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class GitCommitCommandExecutor implements GitCommandExecutor {

  @NonNull GitLogsService logsService;
  @NonNull UserProvider userProvider;

  @Override
  public String getCommandClass() {
    return GitCommitSubCommand.class.getSimpleName();
  }

  @Override
  public Mono<Void> execute(final Owner owner,
                            final Git git,
                            final GitSubCommand command) {
    return userProvider.getAuthenticatedUser().flatMap(user -> Mono.fromCallable(() -> {
      final var commitCommand = (GitCommitSubCommand) command;
      final var cmd = git.commit();
      cmd.setMessage(commitCommand.getMessage());
      cmd.setCommitter(user.getUsername(), user.getEmail());
      cmd.setAuthor(user.getUsername(), user.getEmail());
      Optional.ofNullable(commitCommand.getAll()).ifPresent(cmd::setAll);
      Optional.ofNullable(commitCommand.getAmend()).ifPresent(cmd::setAmend);
      commitCommand.getOnly().forEach(cmd::setOnly);
      Optional.ofNullable(commitCommand.getMessage()).ifPresent(cmd::setMessage);
      cmd.call();
//      cmd.setHookOutputStream()
      logsService.add(owner, "Commit done");
      return null;
    }));
  }
}
