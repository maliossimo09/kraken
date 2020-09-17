package com.octoperf.kraken.git.service.cmd;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum GitSubCommand {
  add(false, false),
  commit(false, false),
  fetch(false, true),
  status(false, false),
  branch(true, true),
  checkout(true, true),
  merge(true, true),
  pull(true, true),
  push(true, true),
  rebase(true, true),
  rm(true, false),
  help(false, false);

 boolean refresh;
 boolean remote;
}
