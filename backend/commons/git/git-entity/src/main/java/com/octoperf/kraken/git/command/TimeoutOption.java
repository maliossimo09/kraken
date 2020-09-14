package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

@Value
@CommandLine.Command
public class TimeoutOption {

  @CommandLine.Option(names = {"--timeout"}, description = "Abort connection if no activity")
  Integer timeout;

  @JsonCreator
  @Builder(toBuilder = true)
  public TimeoutOption(@JsonProperty("timeout") final Integer timeout) {
    this.timeout = timeout;
  }
}
