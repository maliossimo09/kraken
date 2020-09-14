package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitSubCommand;
import com.octoperf.kraken.git.entity.command.GitCommitSubCommand;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class GitCommitCommandExecutor implements GitCommandExecutor {

  @NonNull UserProvider userProvider;

  @Override
  public String getCommandClass() {
    return GitCommitSubCommand.class.getSimpleName();
  }

  @Override
  public Mono<Void> execute(final Git git,
                            final GitSubCommand command) {
    return userProvider.getAuthenticatedUser().flatMap(user -> Mono.fromCallable(() -> {
      final var commitCommand = (GitCommitSubCommand) command;
      final var commit = git.commit();
      commit.setMessage(commitCommand.getMessage());
      commit.setCommitter(user.getUsername(), user.getEmail());
      commit.setAuthor(user.getUsername(), user.getEmail());
//      commitCommand.getAll().ifPresent(commit::setAll);
//      commitCommand.getOnly().forEach(commit::setOnly);
//      commitCommand.getAmend().ifPresent(commit::setAmend);
//      commitCommand.getAllowEmpty().ifPresent(commit::setAllowEmpty);
//      commitCommand.getNoVerify().ifPresent(commit::setNoVerify);
      commit.call();
      return null;
    }));
  }
}
