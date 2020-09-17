package com.octoperf.kraken.git.entity;

import lombok.Builder;
import lombok.Value;

/**
 * 2 <XY> <sub> <mH> <mI> <mW> <hH> <hI> <X><score> <path><sep><origPath>
 */
@Value
@Builder(toBuilder = true)
public class GitRenamedCopiedStatus {
  // <XY> A 2 character field containing the staged and unstaged XY values described in the short format, with unchanged indicated by a "." rather than a space.
  String xy;
  // <X><score>  The rename or copy score (denoting the percentage of similarity between the source and target of the move or copy). For example "R100" or "C75".
  String score;
  // <path> The pathname. In a renamed/copied entry, this is the target path.
  String path;
  // <origPath>  The pathname in the commit at HEAD or in the index. This is only present in a renamed/copied entry, and tells where the renamed/copied contents came from.
  String origPath;
}
