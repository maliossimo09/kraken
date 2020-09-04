package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
public class GitPushCommand implements GitCommand {

  Optional<String> remote;
  Optional<Boolean> force;
  Optional<Boolean> dryRun;
  Optional<Boolean> atomic;
  List<String> options;

  @JsonCreator
  public GitPushCommand(@JsonProperty("remote") final String remote,
                        @JsonProperty("force") final Boolean force,
                        @JsonProperty("dryRun") final Boolean dryRun,
                        @JsonProperty("atomic") final Boolean atomic,
                        @JsonProperty("options") final List<String> options) {
    this.remote = Optional.ofNullable(remote);
    this.force = Optional.ofNullable(force);
    this.dryRun = Optional.ofNullable(dryRun);
    this.atomic = Optional.ofNullable(atomic);
    this.options = Optional.ofNullable(options).orElse(ImmutableList.of());
  }

  @Builder(toBuilder = true)
  private GitPushCommand(@NonNull final Optional<String> remote,
                        @NonNull final Optional<Boolean> force,
                        @NonNull final Optional<Boolean> dryRun,
                        @NonNull final Optional<Boolean> atomic,
                        @NonNull final List<String> options) {
    this.remote = remote;
    this.force = force;
    this.dryRun = dryRun;
    this.atomic = atomic;
    this.options = options;
  }
}
