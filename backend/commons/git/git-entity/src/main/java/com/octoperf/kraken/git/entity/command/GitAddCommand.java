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
public class GitAddCommand implements GitCommand {
  List<String> filePatterns;
  Optional<Boolean> update;

  @JsonCreator
  public GitAddCommand(@JsonProperty("filePatterns") final List<String> filePatterns,
                       @JsonProperty("update") final Boolean update) {
    this.filePatterns = Optional.ofNullable(filePatterns).orElse(ImmutableList.of());
    this.update = Optional.ofNullable(update);
  }

  @Builder(toBuilder = true)
  private GitAddCommand(@NonNull List<String> filePatterns,
                        @NonNull Optional<Boolean> update) {
    this.filePatterns = filePatterns;
    this.update = update;
  }
}
