package com.octoperf.kraken.git.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Multimap;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class GitStatus {

  Multimap<String, GitFileStatus> diff;
  boolean hasUncommittedChanges;
  boolean isClean;

  @JsonCreator
  GitStatus(
      @NonNull @JsonProperty("diff") final Multimap<String, GitFileStatus> diff,
      @NonNull @JsonProperty("hasUncommittedChanges") final boolean hasUncommittedChanges,
      @NonNull @JsonProperty("isClean") final boolean isClean
  ) {
    super();
    this.diff = diff;
    this.hasUncommittedChanges = hasUncommittedChanges;
    this.isClean = isClean;
  }
}
