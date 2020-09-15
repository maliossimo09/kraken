package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

import java.util.List;
import java.util.Optional;

@Value
@CommandLine.Command
public class FilePatternsParameters {

  @CommandLine.Parameters(arity = "1..*", paramLabel = "file pattern")
  List<String> filePatterns;

  @JsonCreator
  @Builder(toBuilder = true)
  public FilePatternsParameters(@JsonProperty("filePatterns") final List<String> filePatterns) {
    this.filePatterns = Optional.ofNullable(filePatterns).orElse(ImmutableList.of());
  }
}
