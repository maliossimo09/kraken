package com.octoperf.kraken.git.entity;

import lombok.Builder;
import lombok.Value;

/**
 * u <xy> <sub> <m1> <m2> <m3> <mW> <h1> <h2> <h3> <path>
 */
@Value
@Builder(toBuilder = true)
public class GitUnmergedStatus {
  // <XY> A 2 character field containing the staged and unstaged XY values described in the short format, with unchanged indicated by a "." rather than a space.
  String xy;
  // <path> The pathname.
  String path;
}
