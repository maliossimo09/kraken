package com.octoperf.kraken.git.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Multimap;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder(toBuilder = true)
public class GitStatus {

  String repositoryState;
  String repositoryStateDescription;
  Multimap<String, GitFileStatus> diff;
  Map<String, String> conflicts;
  boolean uncommittedChanges;
  boolean clean;

  @JsonCreator
  GitStatus(
      @NonNull @JsonProperty("repositoryState") final String repositoryState,
      @NonNull @JsonProperty("repositoryStateDescription") final String repositoryStateDescription,
      @NonNull @JsonProperty("diff") final Multimap<String, GitFileStatus> diff,
      @NonNull @JsonProperty("conflicts") final Map<String, String> conflicts,
      @NonNull @JsonProperty("uncommittedChanges") final Boolean uncommittedChanges,
      @NonNull @JsonProperty("clean") final Boolean clean
  ) {
    super();
    this.repositoryState = repositoryState;
    this.repositoryStateDescription = repositoryStateDescription;
    this.diff = diff;
    this.conflicts = conflicts;
    this.uncommittedChanges = uncommittedChanges;
    this.clean = clean;
  }
}
