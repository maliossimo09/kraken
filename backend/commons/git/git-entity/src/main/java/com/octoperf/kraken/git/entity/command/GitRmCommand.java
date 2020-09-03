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
@Builder(toBuilder = true)
public class GitRmCommand implements GitCommand {
  List<String> filePatterns;
  Optional<Boolean> cached;

  @JsonCreator
  public GitRmCommand(@JsonProperty("filePatterns") final List<String> filePatterns,
                      @JsonProperty("cached") final Boolean cached) {
    this.filePatterns = Optional.ofNullable(filePatterns).orElse(ImmutableList.of());
    this.cached = Optional.ofNullable(cached);
  }

  @Builder(toBuilder = true)
  private GitRmCommand(@NonNull List<String> filePatterns,
                      @NonNull Optional<Boolean> cached) {
    this.filePatterns = filePatterns;
    this.cached = cached;
  }
}
