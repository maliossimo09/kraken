package com.octoperf.kraken.git.service.jgit.command;

import com.octoperf.kraken.git.entity.command.GitCommitCommand;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class GitCommitCommandExecutor implements GitCommandExecutor<GitCommitCommand> {

  @NonNull UserProvider userProvider;

  @Override
  public Class<GitCommitCommand> getCommandClass() {
    return GitCommitCommand.class;
  }

  @Override
  public Mono<Void> execute(final Git git,
                            final TransportConfigCallback transportConfigCallback,
                            final Path root,
                            final GitCommitCommand command) {
    return userProvider.getAuthenticatedUser().flatMap(user -> Mono.fromCallable(() -> {
      final var commit = git.commit();
      commit.setMessage(command.getMessage());
      commit.setCommitter(user.getUsername(), user.getEmail());
      commit.setAuthor(user.getUsername(), user.getEmail());
      command.getAll().ifPresent(commit::setAll);
      command.getOnly().forEach(commit::setOnly);
      command.getAmend().ifPresent(commit::setAmend);
      command.getAllowEmpty().ifPresent(commit::setAllowEmpty);
      command.getNoVerify().ifPresent(commit::setNoVerify);
      commit.call();
      return null;
    }));
  }
}
