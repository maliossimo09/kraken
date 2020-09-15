package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.octoperf.kraken.security.entity.owner.Owned;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class GitCommandLog implements Owned {

  Owner owner;
  String text;

  @JsonCreator
  GitCommandLog(
      @NonNull @JsonProperty("owner") final Owner owner,
      @NonNull @JsonProperty("text") final String text
  ) {
    super();
    this.owner = owner;
    this.text = text;
  }

}
