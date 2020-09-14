package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

import java.util.Optional;

@Value
@CommandLine.Command(name = "add", description = "Add file contents to the index")
public class GitAddSubCommand implements GitSubCommand {
  @CommandLine.Option(names = {"-u", "--update"}, description = "Only match <filepattern> against already tracked files in the index rather than the working tree")
  Boolean update;

  @CommandLine.Mixin
  FilePatternsParameters filePatterns;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitAddSubCommand(@JsonProperty("filePatterns") final FilePatternsParameters filePatterns,
                          @JsonProperty("update") final Boolean update) {
    this.filePatterns = Optional.ofNullable(filePatterns).orElse(FilePatternsParameters.builder().build());
    this.update = update;
  }

  public GitAddSubCommand() {
    this(null, null);
  }
}
