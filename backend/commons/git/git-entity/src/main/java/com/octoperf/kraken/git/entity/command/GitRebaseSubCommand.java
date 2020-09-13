package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
public class GitRebaseSubCommand implements GitSubCommand {

  Optional<String> upstream;
  Optional<Boolean> preserveMerge;
  Optional<MergeStrategy> strategy;
  Optional<RebaseOperation> operation;

  @JsonCreator
  public GitRebaseSubCommand(@JsonProperty("upstream") final String upstream,
                             @JsonProperty("preserveMerge") final Boolean preserveMerge,
                             @JsonProperty("strategy") final MergeStrategy strategy,
                             @JsonProperty("operation") final RebaseOperation operation) {
    this.upstream = Optional.ofNullable(upstream);
    this.preserveMerge = Optional.ofNullable(preserveMerge);
    this.strategy = Optional.ofNullable(strategy);
    this.operation = Optional.ofNullable(operation);
  }

  @Builder(toBuilder = true)
  private GitRebaseSubCommand(@NonNull final Optional<String> upstream,
                              @NonNull final Optional<Boolean> preserveMerge,
                              @NonNull final Optional<MergeStrategy> strategy,
                              @NonNull final Optional<RebaseOperation> operation) {
    this.upstream = upstream;
    this.preserveMerge = preserveMerge;
    this.strategy = strategy;
    this.operation = operation;
  }
}
