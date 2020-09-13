package com.octoperf.kraken.git.service.jgit;

import lombok.Value;
import picocli.CommandLine;

import java.io.File;

@Value
public class Tar {
  @CommandLine.Option(names = "-c", description = "create a new archive")
  Boolean create;

  @CommandLine.Option(names = {"-f", "--file"}, paramLabel = "ARCHIVE", description = "the archive file")
  File archive;

  @CommandLine.Parameters(paramLabel = "FILE", description = "one ore more files to archive")
  File[] files;

  @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  Boolean helpRequested;

  public Tar(){
    this.create = null;
    this.archive = null;
    this.files = new File[0];
    this.helpRequested = null;
  }
}