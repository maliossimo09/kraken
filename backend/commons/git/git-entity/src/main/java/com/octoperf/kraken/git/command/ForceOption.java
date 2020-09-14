package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

@Value
@CommandLine.Command
public class ForceOption {

  @CommandLine.Option(names = {"-f", "--force"})
  Boolean force;

  @JsonCreator
  @Builder(toBuilder = true)
  public ForceOption(@JsonProperty("force") final Boolean force) {
    this.force = force;
  }
}
