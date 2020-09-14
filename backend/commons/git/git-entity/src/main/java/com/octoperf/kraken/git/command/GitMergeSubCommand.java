package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import picocli.CommandLine;

import java.util.Optional;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@CommandLine.Command(name = "merge", description = "Merges two development histories")
public class GitMergeSubCommand implements GitSubCommand {

  @CommandLine.Mixin
  final MergeStrategyOption strategy;

  @CommandLine.Option(names = {"--squash"}, description = "Squash commits as if a real merge happened, but do not make a commit or move the HEAD.")
  final Boolean squash;

  @CommandLine.Option(names = {"--no-commit"}, description = "Do not commit after a successful merge")
  final Boolean noCommit;

  @CommandLine.Parameters(paramLabel = "REF", description = "Ref to be merged", index = "0")
  String ref;

  @CommandLine.Mixin
  final FastForwardOption ff;

  @CommandLine.Option(names = {"-m"}, description = "Set the commit message to be used for the merge commit (in case one is created).")
  String message;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitMergeSubCommand(@JsonProperty("strategy") final MergeStrategyOption strategy,
                            @JsonProperty("squash") final Boolean squash,
                            @JsonProperty("noCommit") final Boolean noCommit,
                            @JsonProperty("ref") final String ref,
                            @JsonProperty("ff") final FastForwardOption ff,
                            @JsonProperty("message") final String message) {
    this.strategy = Optional.ofNullable(strategy).orElse(MergeStrategyOption.builder().build());
    this.squash = squash;
    this.noCommit = noCommit;
    this.ref = ref;
    this.ff = Optional.ofNullable(ff).orElse(FastForwardOption.builder().build());
    this.message = message;
  }

  public GitMergeSubCommand() {
    this(null,
        null,
        null,
        null,
        null,
        null);
  }
}

