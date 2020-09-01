package com.octoperf.kraken.git.entity;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class GitStatus {

  List<String> ignoredNotInIndex;
  List<String> added;
  List<String> conflicting;
  List<String> untracked;
  List<String> untrackedFolders;
  List<String> changed;
  List<String> missing;
  List<String> uncommittedChanges;
  List<String> removed;
  boolean hasUncommittedChanges;
  boolean isClean;

//  System.out.println(status.getIgnoredNotInIndex());
//    System.out.println(status.getAdded());
//    System.out.println(status.getConflicting());
//    System.out.println(status.getUntracked());
//    System.out.println(status.getUntrackedFolders());
//    System.out.println(status.getChanged());
//    System.out.println(status.getMissing());
//    System.out.println(status.getUncommittedChanges());
//    System.out.println(status.hasUncommittedChanges());
//    System.out.println(status.getRemoved());
//    System.out.println(status.isClean());
}
