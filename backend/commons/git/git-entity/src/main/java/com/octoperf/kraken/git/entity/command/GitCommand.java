package com.octoperf.kraken.git.entity.command;

import picocli.CommandLine;

@CommandLine.Command(name = "git", subcommands = {
    GitAddSubCommand.class,
})
public class GitCommand {
}
