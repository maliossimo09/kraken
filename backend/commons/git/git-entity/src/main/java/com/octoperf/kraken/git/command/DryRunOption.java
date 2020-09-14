package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

@Value
@CommandLine.Command
public class DryRunOption {

  @CommandLine.Option(names = {"--dry-run"})
  Boolean dryRun;

  @JsonCreator
  @Builder(toBuilder = true)
  public DryRunOption(@JsonProperty("dryRun") final Boolean dryRun) {
    this.dryRun = dryRun;
  }
}
