package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
public class GitPullCommand implements GitCommand {

  Optional<MergeStrategy> strategy;
  Optional<RebaseMode> rebase;
  Optional<FastForwardMode> fastForward;
  Optional<String> remote;

  @JsonCreator
  public GitPullCommand(@JsonProperty("strategy") final MergeStrategy strategy,
                        @JsonProperty("rebase") final RebaseMode rebase,
                        @JsonProperty("fastForward") final FastForwardMode fastForward,
                        @JsonProperty("remote") final String remote) {
    this.strategy = Optional.ofNullable(strategy);
    this.rebase = Optional.ofNullable(rebase);
    this.fastForward = Optional.ofNullable(fastForward);
    this.remote = Optional.ofNullable(remote);
  }

  @Builder(toBuilder = true)
  private GitPullCommand(@NonNull final Optional<MergeStrategy> strategy,
                         @NonNull final Optional<RebaseMode> rebase,
                         @NonNull final Optional<FastForwardMode> fastForward,
                         @NonNull final Optional<String> remote) {
    this.strategy = strategy;
    this.rebase = rebase;
    this.fastForward = fastForward;
    this.remote = remote;
  }

}
