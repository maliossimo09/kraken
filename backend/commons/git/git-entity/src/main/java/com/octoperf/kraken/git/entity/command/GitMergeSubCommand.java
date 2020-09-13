package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
public class GitMergeSubCommand implements GitSubCommand {

  Optional<String> message;
  Optional<Boolean> squash;
  Optional<FastForwardMode> fastForward;
  Optional<Boolean> commit;
  Optional<MergeStrategy> strategy;

  @JsonCreator
  public GitMergeSubCommand(@JsonProperty("message") final String message,
                            @JsonProperty("squash") final Boolean squash,
                            @JsonProperty("fastForward") final FastForwardMode fastForward,
                            @JsonProperty("commit") final Boolean commit,
                            @JsonProperty("strategy") final MergeStrategy strategy) {
    this.message = Optional.ofNullable(message);
    this.squash = Optional.ofNullable(squash);
    this.fastForward = Optional.ofNullable(fastForward);
    this.commit = Optional.ofNullable(commit);
    this.strategy = Optional.ofNullable(strategy);
  }

  @Builder(toBuilder = true)
  private GitMergeSubCommand(@NonNull final Optional<String> message,
                             @NonNull final Optional<Boolean> squash,
                             @NonNull final Optional<FastForwardMode> fastForward,
                             @NonNull final Optional<Boolean> commit,
                             @NonNull final Optional<MergeStrategy> strategy) {
    this.message = message;
    this.squash = squash;
    this.fastForward = fastForward;
    this.commit = commit;
    this.strategy = strategy;
  }
}
