package com.octoperf.kraken.git.entity;

public enum GitRepositoryState {
  BOTH_DELETED,
  ADDED_BY_US,
  DELETED_BY_THEM,
  ADDED_BY_THEM,
  DELETED_BY_US,
  BOTH_ADDED,
  BOTH_MODIFIED;
}
