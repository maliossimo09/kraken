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
@CommandLine.Command(name = "rebase", description = "Reapply commits on top of another base tip")
public class GitRebaseSubCommand implements GitSubCommand {

  @CommandLine.Mixin
  final MergeStrategyOption strategy;

  @CommandLine.Option(names = {"--onto"}, description = "Starting point at which to create the new commits. If the --onto option is not specified, the starting point is <upstream>. May be any valid commit, and not just an existing branch name.")
  String onto;

  @CommandLine.Option(names = {"--operation"}, description = "[=begin|continue|skip|abort|process_steps]")
  String operation;

  @CommandLine.Option(names = {"-p", "--preserve-merges"}, description = "Recreate merge commits instead of flattening the history by replaying commits a merge commit introduces.")
  final Boolean preserveMerges;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitRebaseSubCommand(@JsonProperty("strategy") final MergeStrategyOption strategy,
                             @JsonProperty("onto") final String onto,
                             @JsonProperty("operation") final String operation,
                             @JsonProperty("preserveMerges") final Boolean preserveMerges) {
    this.strategy = Optional.ofNullable(strategy).orElse(MergeStrategyOption.builder().build());
    this.onto = onto;
    this.operation = operation;
    this.preserveMerges = preserveMerges;
  }

  public GitRebaseSubCommand() {
    this(null, null, null, null);
  }
}
