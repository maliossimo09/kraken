package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

@Value
@CommandLine.Command
public class FastForwardOption {

  @CommandLine.Option(names = {"--ff"}, description = "When the merge resolves as a fast-forward, only update the branch pointer, without creating a merge commit.")
  Boolean ff;

  @CommandLine.Option(names = {"--no-ff"}, description = "Create a merge commit even when the merge resolves as a fast-forward.")
  Boolean noFf;

  @CommandLine.Option(names = {"--ff-only"}, description = "Refuse to merge and exit with a non-zero status unless the current HEAD is already up-to-date or the merge can be resolved as a fast-forward.")
  Boolean ffOnly;

  @JsonCreator
  @Builder(toBuilder = true)
  public FastForwardOption(
      @JsonProperty("ff") final Boolean ff,
      @JsonProperty("noFf") final Boolean noFf,
      @JsonProperty("ffOnly") final Boolean ffOnly) {
    this.ff = ff;
    this.noFf = noFf;
    this.ffOnly = ffOnly;
  }
}
