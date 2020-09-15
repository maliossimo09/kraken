package com.octoperf.kraken.git.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class GitLog {
  String id;
  Long time;
  GitIdentity author;
  GitIdentity committer;
  String message;
  String encoding;
  String path;

  @JsonCreator
  public GitLog(@NonNull @JsonProperty("id") final String id,
                @NonNull @JsonProperty("time") final Long time,
                @NonNull @JsonProperty("author") final GitIdentity author,
                @NonNull @JsonProperty("committer") final GitIdentity committer,
                @NonNull @JsonProperty("message") final String message,
                @NonNull @JsonProperty("encoding") final String encoding,
                @NonNull @JsonProperty("path") final String path) {
    this.id = id;
    this.time = time;
    this.author = author;
    this.committer = committer;
    this.message = message;
    this.encoding = encoding;
    this.path = path;
  }
}
