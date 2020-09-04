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
public class GitResetCommand implements GitCommand {

  Optional<String> ref;
  Optional<ResetType> type;
  List<String> path;
  Optional<Boolean> disableRefLog;

  @JsonCreator
  public GitResetCommand(@JsonProperty("ref") final String ref,
                         @JsonProperty("type") final ResetType type,
                         @JsonProperty("path") final List<String> path,
                         @JsonProperty("disableRefLog") final Boolean disableRefLog) {
    this.ref = Optional.ofNullable(ref);
    this.type = Optional.ofNullable(type);
    this.path = Optional.ofNullable(path).orElse(ImmutableList.of());
    this.disableRefLog = Optional.ofNullable(disableRefLog);
  }

  @Builder(toBuilder = true)
  private GitResetCommand(@NonNull final Optional<String> ref,
                          @NonNull final Optional<ResetType> type,
                          @NonNull final List<String> path,
                          @NonNull final Optional<Boolean> disableRefLog) {
    this.ref = ref;
    this.type = type;
    this.path = path;
    this.disableRefLog = disableRefLog;
  }
}
