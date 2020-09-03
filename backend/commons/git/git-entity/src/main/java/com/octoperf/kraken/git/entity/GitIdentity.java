package com.octoperf.kraken.git.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class GitIdentity {
  String name;
  String email;

  @JsonCreator
  public GitIdentity(@NonNull @JsonProperty("name") final String name,
                     @NonNull @JsonProperty("email") final String email) {
    this.name = name;
    this.email = email;
  }
}
