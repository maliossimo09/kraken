package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import picocli.CommandLine;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@CommandLine.Command(name = "merge", description = "Merges two development histories")
public class GitMergeSubCommand implements GitSubCommand {

  @CommandLine.Option(names = {"-s", "--strategy"}, description = "Use the given merge strategy. Can be supplied more than once to specify them in the order they should be tried. If there is no -s option, the recursive strategy is used. Currently the following strategies are supported: ours, theirs, simple-two-way-in-core, resolve, recursive")
  String strategyName;

  @CommandLine.Option(names = {"--squash"}, description = "Squash commits as if a real merge happened, but do not make a commit or move the HEAD.")
  final Boolean squash;

  @CommandLine.Option(names = {"--no-commit"}, description = "Do not commit after a successful merge")
  final Boolean noCommit;

  @CommandLine.Parameters(paramLabel = "REF", description = "Ref to be merged", index = "0")
  String ref;

  @CommandLine.Option(names = {"--ff"}, description = "When the merge resolves as a fast-forward, only update the branch pointer, without creating a merge commit.")
  final Boolean ff;

  @CommandLine.Option(names = {"--no-ff"}, description = "Create a merge commit even when the merge resolves as a fast-forward.")
  final Boolean noFf;

  @CommandLine.Option(names = {"--ff-only"}, description = "Refuse to merge and exit with a non-zero status unless the current HEAD is already up-to-date or the merge can be resolved as a fast-forward.")
  final Boolean ffOnly;

  @CommandLine.Option(names = {"-m"}, description = "Set the commit message to be used for the merge commit (in case one is created).")
  String message;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitMergeSubCommand(@JsonProperty("strategyName") final String strategyName,
                            @JsonProperty("squash") final Boolean squash,
                            @JsonProperty("noCommit") final Boolean noCommit,
                            @JsonProperty("ref") final String ref,
                            @JsonProperty("ff") final Boolean ff,
                            @JsonProperty("noFf") final Boolean noFf,
                            @JsonProperty("ffOnly") final Boolean ffOnly,
                            @JsonProperty("message") final String message) {
    this.strategyName = strategyName;
    this.squash = squash;
    this.noCommit = noCommit;
    this.ref = ref;
    this.ff = ff;
    this.noFf = noFf;
    this.ffOnly = ffOnly;
    this.message = message;
  }

  public GitMergeSubCommand() {
    this(null,
        null,
        null,
        null,
        null,
        null,
        null,
        null);
  }
}

