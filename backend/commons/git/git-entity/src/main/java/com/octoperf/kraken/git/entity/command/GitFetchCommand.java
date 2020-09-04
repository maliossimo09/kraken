package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
public class GitFetchCommand implements GitCommand {
  Optional<String> remote;
  Optional<Boolean> forceUpdate;
  Optional<Boolean> dryRun;

  @JsonCreator
  public GitFetchCommand(@JsonProperty("remote") final String remote,
                         @JsonProperty("forceUpdate") final Boolean forceUpdate,
                         @JsonProperty("dryRun") final Boolean dryRun) {
    this.remote = Optional.ofNullable(remote);
    this.forceUpdate = Optional.ofNullable(forceUpdate);
    this.dryRun = Optional.ofNullable(dryRun);
  }

  @Builder(toBuilder = true)
  private GitFetchCommand(@NonNull Optional<String> remote,
                          @NonNull Optional<Boolean> forceUpdate,
                          @NonNull Optional<Boolean> dryRun) {
    this.remote = remote;
    this.forceUpdate = forceUpdate;
    this.dryRun = dryRun;
  }
}
