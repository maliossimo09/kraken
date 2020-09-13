package com.octoperf.kraken.git.entity.command;

import lombok.Builder;
import lombok.Value;
import picocli.CommandLine;

import java.util.Optional;

@CommandLine.Command(name = "git", subcommands = {
    CommandLine.HelpCommand.class,
    GitAddSubCommand.class,
    GitCommitSubCommand.class,
    GitFetchSubCommand.class,
    GitMergeSubCommand.class
})
@Value
public class GitCommand {

  @CommandLine.Option(names = {"-V", "--version"}, versionHelp = true, description = "display version info")
  Boolean versionInfoRequested;

  @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
  Boolean usageHelpRequested;

  @Builder(toBuilder = true)
  public GitCommand(final Boolean versionInfoRequested,
                    final Boolean usageHelpRequested) {
    this.versionInfoRequested = Optional.ofNullable(versionInfoRequested).orElse(false);
    this.usageHelpRequested = Optional.ofNullable(usageHelpRequested).orElse(false);
  }

  public GitCommand() {
    this(null, null);
  }
}
