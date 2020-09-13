package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

import java.util.List;
import java.util.Optional;

@Value
@CommandLine.Command(name = "add", description = "Add file contents to the index")
public class GitAddSubCommand implements GitSubCommand {
  @CommandLine.Option(names = {"-u", "--update"}, description = "Only match <filepattern> against already tracked files in the index rather than the working tree")
  Boolean update;

  @CommandLine.Parameters(arity = "1..*", paramLabel = "filepattern", description = "Files to add content from")
  List<String> filePatterns;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitAddSubCommand(@JsonProperty("filePatterns") final List<String> filePatterns,
                          @JsonProperty("update") final Boolean update) {
    this.filePatterns = Optional.ofNullable(filePatterns).orElse(ImmutableList.of());
    this.update = update;
  }

  public GitAddSubCommand() {
    this(null, null);
  }
}
