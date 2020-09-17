package com.octoperf.kraken.git.service.cmd;

public enum GitSubCommand {
  add(false),
  commit(false),
  fetch(false),
  status(false),
  branch(true),
  checkout(true),
  merge(true),
  pull(true),
  push(true),
  rebase(true),
  rm(true),
  help(false);

  private boolean refresh;

  GitSubCommand(final boolean refresh) {
    this.refresh = refresh;
  }

  public boolean isRefresh() {
    return refresh;
  }
}
