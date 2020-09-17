package com.octoperf.kraken.git.entity;

import lombok.Builder;
import lombok.Value;

/**
 * 1 <XY> <sub> <mH> <mI> <mW> <hH> <hI> <path>
 */
@Value
@Builder(toBuilder = true)
public class GitChangedStatus {
  // <XY> A 2 character field containing the staged and unstaged XY values described in the short format, with unchanged indicated by a "." rather than a space.
  String xy;
  // <path> The pathname
  String path;
}
