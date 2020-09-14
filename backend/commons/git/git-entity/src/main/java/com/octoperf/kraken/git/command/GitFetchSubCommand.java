package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

import java.util.Optional;

@Value
@CommandLine.Command(name = "fetch", description = "Update remote refs from another repository")
public class GitFetchSubCommand implements GitSubCommand {

  @CommandLine.Mixin
  TimeoutOption timeout;

  @CommandLine.Option(names = {"--fsck"}, description = "Perform fsck style checks on receive")
  Boolean fsck;

  @CommandLine.Option(names = {"--prune"}, description = "Prune stale tracking refs")
  Boolean prune;

  @CommandLine.Mixin
  DryRunOption dryRun;

  @CommandLine.Mixin
  ThinOption thin;

  @CommandLine.Option(names = {"--quiet"}, description = "Do not show progress messages")
  Boolean quiet;

  @CommandLine.Option(names = {"-t", "--tags"}, description = "Fetch all tags")
  Boolean tags;

  @CommandLine.Mixin
  ForceOption force;

  @CommandLine.Mixin
  RemoteParameters remote;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitFetchSubCommand(@JsonProperty("timeout") final TimeoutOption timeout,
                            @JsonProperty("fsck") final Boolean fsck,
                            @JsonProperty("prune") final Boolean prune,
                            @JsonProperty("dryRun") final DryRunOption dryRun,
                            @JsonProperty("thin") final ThinOption thin,
                            @JsonProperty("quiet") final Boolean quiet,
                            @JsonProperty("tags") final Boolean tags,
                            @JsonProperty("force") final ForceOption force,
                            @JsonProperty("remote") final RemoteParameters remote) {
    this.timeout = Optional.ofNullable(timeout).orElse(TimeoutOption.builder().build());
    this.fsck = fsck;
    this.prune = prune;
    this.dryRun = Optional.ofNullable(dryRun).orElse(DryRunOption.builder().build());
    this.thin = Optional.ofNullable(thin).orElse(ThinOption.builder().build());
    this.quiet = quiet;
    this.tags = tags;
    this.force = Optional.ofNullable(force).orElse(ForceOption.builder().build());
    this.remote = Optional.ofNullable(remote).orElse(RemoteParameters.builder().build());
  }

  public GitFetchSubCommand() {
    this(null, null, null, null, null, null, null, null, null);
  }
}
