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
  long time;
  String authorEmail;
  String authorName;
  String message;
  String encoding;

  @JsonCreator
  public GitLog(@NonNull @JsonProperty("id") final String id,
                @NonNull @JsonProperty("time") final long time,
                @NonNull @JsonProperty("authorEmail") final String authorEmail,
                @NonNull @JsonProperty("authorName") final String authorName,
                @NonNull @JsonProperty("message") final String message,
                @NonNull @JsonProperty("encoding") final String encoding) {
    this.id = id;
    this.time = time;
    this.authorEmail = authorEmail;
    this.authorName = authorName;
    this.message = message;
    this.encoding = encoding;
  }
}
