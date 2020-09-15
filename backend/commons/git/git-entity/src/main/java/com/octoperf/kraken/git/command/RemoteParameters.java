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
@CommandLine.Command
public class RemoteParameters {

  @CommandLine.Parameters(paramLabel = "uri-ish", index = "0", defaultValue = "origin")
  String remote;

  @JsonCreator
  @Builder(toBuilder = true)
  public RemoteParameters(@JsonProperty("remote") final String remote) {
    this.remote = Optional.ofNullable(remote).orElse("origin");
  }
}
