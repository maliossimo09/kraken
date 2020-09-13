package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import picocli.CommandLine;

import java.util.List;
import java.util.Optional;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@CommandLine.Command(name = "commit", description = "Record changes to the repository")
public class GitCommitSubCommand implements GitSubCommand {

  @CommandLine.Option(names = {"-m", "--message"}, description = "Use the given <msg> as the commit message", required = true)
  String message;

  @CommandLine.Option(names = {"-a", "--all"}, description = "Commit all modified and deleted files")
  final Boolean all;

  @CommandLine.Option(names = {"-o", "--only"}, description = "Commit specified paths only")
  final Boolean only;

  @CommandLine.Option(names = {"--amend"}, description = "Amend the tip of the current branch")
  final Boolean amend;

  @CommandLine.Parameters(paramLabel = "paths", description = "See --only")
  final List<String> paths;

  @JsonCreator
  @Builder(toBuilder = true)
  public GitCommitSubCommand(@JsonProperty("message") final String message,
                             @JsonProperty("all") final Boolean all,
                             @JsonProperty("only") final Boolean only,
                             @JsonProperty("amend") final Boolean amend,
                             @JsonProperty("paths") final List<String> paths) {
    this.message = message;
    this.all = all;
    this.only = only;
    this.amend = amend;
    this.paths = Optional.ofNullable(paths).orElse(ImmutableList.of());
  }

  private GitCommitSubCommand() {
    this(null, null, null, null, null);
  }
}
