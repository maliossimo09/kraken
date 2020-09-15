package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import picocli.CommandLine;

import java.util.List;
import java.util.Optional;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@CommandLine.Command(name = "reset", description = "Reset current HEAD to the specified state")
public class GitResetSubCommand implements GitSubCommand {

  @CommandLine.Option(names = {"--soft"}, description = "Resets without touching the index file nor the working tree")
  final Boolean soft;

  @CommandLine.Option(names = {"--mixed"}, description = "Resets the index but not the working tree")
  final Boolean mixed;

  @CommandLine.Option(names = {"--hard"}, description = "Resets the index and working tree")
  final Boolean hard;

  @CommandLine.Parameters(paramLabel = "commit-ish", description = "Reset to given reference name", index = "0", arity = "0..1")
  String commit;

  @CommandLine.Parameters(paramLabel = "commit-ish", description = "Paths to reset", index = "1", arity = "0..*")
  final List<String> paths;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitResetSubCommand(@JsonProperty("soft") final Boolean soft,
                            @JsonProperty("mixed") final Boolean mixed,
                            @JsonProperty("hard") final Boolean hard,
                            @JsonProperty("commit") final String commit,
                            @JsonProperty("paths") final List<String> paths) {
    this.soft = soft;
    this.mixed = mixed;
    this.hard = hard;
    this.commit = commit;
    this.paths = Optional.ofNullable(paths).orElse(ImmutableList.of());
  }

  public GitResetSubCommand() {
    this(null, null, null, null, null);
  }
}
