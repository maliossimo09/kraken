package com.octoperf.kraken.git.entity;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class GitStatus {

  GitBranchStatus branch;
  List<GitChangedStatus> changed;
  List<GitRenamedCopiedStatus> renamedCopied;
  List<GitUnmergedStatus> unmerged;
  List<String> untracked;
  List<String> ignored;

}
