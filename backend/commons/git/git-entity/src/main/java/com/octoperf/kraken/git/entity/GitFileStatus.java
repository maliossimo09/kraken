package com.octoperf.kraken.git.entity;

public enum GitFileStatus {
  IGNORED_NOT_IN_INDEX,
  ADDED,
  CONFLICTING,
  UNTRACKED,
  CHANGED,
  MISSING,
  REMOVED,
  MODIFIED
}
