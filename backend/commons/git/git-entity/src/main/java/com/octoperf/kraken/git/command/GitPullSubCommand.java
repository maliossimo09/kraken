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
@CommandLine.Command(name = "pull", description = "Incorporates changes from a remote repository into the current branch")
public class GitPullSubCommand implements GitSubCommand {

  @CommandLine.Mixin
  final FastForwardOption ff;

  @CommandLine.Mixin
  final MergeStrategyOption strategy;

  @CommandLine.Mixin
  final RemoteParameters remote;

  @CommandLine.Option(names = {"-r", "--rebase"}, description = "[=false|true|preserve|interactive]")
  String rebase;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitPullSubCommand(@JsonProperty("ff") final FastForwardOption ff,
                           @JsonProperty("strategy") final MergeStrategyOption strategy,
                           @JsonProperty("remote") final RemoteParameters remote,
                           @JsonProperty("rebase") final String rebase) {
    this.ff = Optional.ofNullable(ff).orElse(FastForwardOption.builder().build());
    this.strategy = Optional.ofNullable(strategy).orElse(MergeStrategyOption.builder().build());
    this.remote = Optional.ofNullable(remote).orElse(RemoteParameters.builder().build());
    this.rebase = rebase;
  }

  public GitPullSubCommand() {
    this(null, null, null, null);

  }
}
