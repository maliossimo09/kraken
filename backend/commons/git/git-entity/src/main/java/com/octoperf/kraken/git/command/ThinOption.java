package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

@Value
@CommandLine.Command
public class ThinOption {

  @CommandLine.Option(names = {"--thin"}, negatable = true)
  Boolean thin;

  @JsonCreator
  @Builder(toBuilder = true)
  public ThinOption(@JsonProperty("thin") final Boolean thin) {
    this.thin = thin;
  }
}
