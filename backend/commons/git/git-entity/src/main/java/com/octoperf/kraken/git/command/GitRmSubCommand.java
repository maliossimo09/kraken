package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

import java.util.Optional;

@Value
@CommandLine.Command(name = "rm", description = "Remove files matching filePatterns from the index, or from the working tree and the index.")
public class GitRmSubCommand implements GitSubCommand {

  @CommandLine.Mixin
  FilePatternsParameters filePatterns;

  @CommandLine.Option(names = {"--cached"}, description = "Remove the specified files only from the index, not from the working directory.")
  Boolean cached;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitRmSubCommand(@JsonProperty("filePatterns") final FilePatternsParameters filePatterns,
                         @JsonProperty("cached") final Boolean cached) {
    this.filePatterns = Optional.ofNullable(filePatterns).orElse(FilePatternsParameters.builder().build());
    this.cached = cached;
  }

  public GitRmSubCommand() {
    this(null, null);
  }
}
