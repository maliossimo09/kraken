package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import picocli.CommandLine;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@CommandLine.Command(name = "fetch", description = "Update remote refs from another repository")
public class GitFetchSubCommand implements GitSubCommand {

  @CommandLine.Option(names = {"--timeout"}, description = "Abort connection if no activity")
  final Integer timeout;

  @CommandLine.Option(names = {"--fsck"}, description = "Perform fsck style checks on receive")
  final Boolean fsck;

  @CommandLine.Option(names = {"--prune"}, description = "Prune stale tracking refs")
  final Boolean prune;

  @CommandLine.Option(names = {"--dry-run"}, description = "Prune stale tracking refs")
  final Boolean dryRun;

  @CommandLine.Option(names = {"--thin"}, description = "Fetch thin pack")
  final Boolean thin;

  @CommandLine.Option(names = {"--quiet"}, description = "Do not show progress messages")
  final Boolean quiet;

  @CommandLine.Option(names = {"-t", "--tags"}, description = "Fetch all tags")
  final Boolean tags;

  @CommandLine.Option(names = {"-f", "--force"}, description = "Force ref update fetch option")
  final Boolean force;

  @CommandLine.Parameters(paramLabel = "uri-ish", index = "0", defaultValue = "origin")
  String remote;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitFetchSubCommand(@JsonProperty("timeout") final Integer timeout,
                            @JsonProperty("fsck") final Boolean fsck,
                            @JsonProperty("prune") final Boolean prune,
                            @JsonProperty("dryRun") final Boolean dryRun,
                            @JsonProperty("thin") final Boolean thin,
                            @JsonProperty("quiet") final Boolean quiet,
                            @JsonProperty("tags") final Boolean tags,
                            @JsonProperty("force") final Boolean force,
                            @JsonProperty("remote") final String remote) {
    this.timeout = timeout;
    this.fsck = fsck;
    this.prune = prune;
    this.dryRun = dryRun;
    this.thin = thin;
    this.quiet = quiet;
    this.tags = tags;
    this.force = force;
    this.remote = remote;
  }

  public GitFetchSubCommand() {
    this(null, null, null, null, null, null, null, null, null);
  }
}
