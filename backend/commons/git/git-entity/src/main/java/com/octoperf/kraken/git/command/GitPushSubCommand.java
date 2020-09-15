package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

import java.util.List;
import java.util.Optional;

@Value
@CommandLine.Command(name = "push", description = "Update remote repository from local refs")
public class GitPushSubCommand implements GitSubCommand {

  @CommandLine.Mixin
  TimeoutOption timeout;

  @CommandLine.Mixin
  RemoteParameters remote;

  @CommandLine.Parameters(paramLabel = "REF", description = "Specify what destination ref to update with what source object.", index = "1", arity = "0..*")
  List<String> refSpecs;

  @CommandLine.Option(names = {"--all"}, description = "Push all branches")
  Boolean all;

  @CommandLine.Option(names = {"--atomic"}, description = "Use an atomic transaction on the remote side if available.", negatable = true)
  Boolean atomic;

  @CommandLine.Option(names = {"--tags"}, description = "All refs under refs/tags are pushed, in addition to refspecs explicitly listed on the command line.")
  Boolean tags;

  @CommandLine.Mixin
  ThinOption thin;

  @CommandLine.Mixin
  ForceOption force;

  @CommandLine.Mixin
  DryRunOption dryRun;

  @CommandLine.Option(names = {"-o", "--push-option"}, description = "Transmit the given string to the server, which passes them to the pre-receive as well as the post-receive hook.")
  List<String> pushOptions;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitPushSubCommand(@JsonProperty("timeout") final TimeoutOption timeout,
                           @JsonProperty("remote") final RemoteParameters remote,
                           @JsonProperty("refSpecs") final List<String> refSpecs,
                           @JsonProperty("all") final Boolean all,
                           @JsonProperty("atomic") final Boolean atomic,
                           @JsonProperty("tags") final Boolean tags,
                           @JsonProperty("thin") final ThinOption thin,
                           @JsonProperty("force") final ForceOption force,
                           @JsonProperty("dryRun") final DryRunOption dryRun,
                           @JsonProperty("pushOptions") final List<String> pushOptions) {
    this.timeout = Optional.ofNullable(timeout).orElse(TimeoutOption.builder().build());
    this.remote = Optional.ofNullable(remote).orElse(RemoteParameters.builder().build());
    this.refSpecs = Optional.ofNullable(refSpecs).orElse(ImmutableList.of());
    this.all = all;
    this.atomic = atomic;
    this.tags = tags;
    this.thin = Optional.ofNullable(thin).orElse(ThinOption.builder().build());
    this.force = Optional.ofNullable(force).orElse(ForceOption.builder().build());
    this.dryRun = Optional.ofNullable(dryRun).orElse(DryRunOption.builder().build());
    this.pushOptions = Optional.ofNullable(pushOptions).orElse(ImmutableList.of());
  }

  public GitPushSubCommand() {
    this(null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null);
  }
}
