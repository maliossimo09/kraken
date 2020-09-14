package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import picocli.CommandLine;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@CommandLine.Command
public class MergeStrategyOption {

  @CommandLine.Option(names = {"-s", "--strategy"}, description = "Use the given merge strategy. Can be supplied more than once to specify them in the order they should be tried. If there is no -s option, the recursive strategy is used. Currently the following strategies are supported: ours, theirs, simple-two-way-in-core, resolve, recursive")
  String strategyName;

  @JsonCreator
  @Builder(toBuilder = true)
  public MergeStrategyOption(@JsonProperty("strategyName") final String strategyName) {
    this.strategyName = strategyName;
  }
}
