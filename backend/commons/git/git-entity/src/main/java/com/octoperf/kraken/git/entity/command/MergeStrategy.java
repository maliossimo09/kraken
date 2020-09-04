package com.octoperf.kraken.git.entity.command;

public enum MergeStrategy {
  OURS,
  THEIRS,
  SIMPLE_TWO_WAY_IN_CORE,
  RESOLVE,
  RECURSIVE
}
