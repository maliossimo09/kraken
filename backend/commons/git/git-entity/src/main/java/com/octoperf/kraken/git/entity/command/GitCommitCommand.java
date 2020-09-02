package com.octoperf.kraken.git.entity.command;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class GitCommitCommand {
  String message;
  boolean all;
  List<String> only;
  boolean amend;

//  @JsonCreator
//  public GitCommitCommand(@NonNull @JsonProperty("filePatterns") final List<String> filePatterns,
//                          @NonNull @JsonProperty("update") final boolean update) {
//    this.filePatterns = filePatterns;
//    this.update = update;
//  }
}
