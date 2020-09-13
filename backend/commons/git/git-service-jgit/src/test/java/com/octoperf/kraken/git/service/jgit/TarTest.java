package com.octoperf.kraken.git.service.jgit;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TarTest {


  @Test
  void shouldTest(){
    String[] args = { "-c", "--file", "result.tar", "file1.txt", "file2.txt" };
    Tar tar = new Tar();
    new CommandLine(tar).parseArgs(args);

//    assert !tar.getHelpRequested();
    assert  tar.getCreate();
    assert  tar.getArchive().equals(new File("result.tar"));
    assert  Arrays.equals(tar.getFiles(), new File[] {new File("file1.txt"), new File("file2.txt")});
  }
}