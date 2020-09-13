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
public class GitResetSubCommand implements GitSubCommand {

  Optional<String> ref;
  Optional<ResetType> resetType;
  List<String> path;
  Optional<Boolean> disableRefLog;

  @JsonCreator
  public GitResetSubCommand(@JsonProperty("ref") final String ref,
                            @JsonProperty("resetType") final ResetType resetType,
                            @JsonProperty("path") final List<String> path,
                            @JsonProperty("disableRefLog") final Boolean disableRefLog) {
    this.ref = Optional.ofNullable(ref);
    this.resetType = Optional.ofNullable(resetType);
    this.path = Optional.ofNullable(path).orElse(ImmutableList.of());
    this.disableRefLog = Optional.ofNullable(disableRefLog);
  }

  @Builder(toBuilder = true)
  private GitResetSubCommand(@NonNull final Optional<String> ref,
                             @NonNull final Optional<ResetType> resetType,
                             @NonNull final List<String> path,
                             @NonNull final Optional<Boolean> disableRefLog) {
    this.ref = ref;
    this.resetType = resetType;
    this.path = path;
    this.disableRefLog = disableRefLog;
  }
}
