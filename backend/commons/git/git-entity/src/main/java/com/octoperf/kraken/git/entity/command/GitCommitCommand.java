package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
@Builder(toBuilder = true)
public class GitCommitCommand implements GitCommand {
  String message;
  Optional<Boolean> all;
  List<String> only;
  Optional<Boolean> amend;
  Optional<Boolean> allowEmpty;
  Optional<Boolean> noVerify;

  @JsonCreator
  public GitCommitCommand(@JsonProperty("message") final String message,
                          @JsonProperty("all") final Boolean all,
                          @JsonProperty("only") final List<String> only,
                          @JsonProperty("amend") final Boolean amend,
                          @JsonProperty("allowEmpty") final Boolean allowEmpty,
                          @JsonProperty("noVerify") final Boolean noVerify) {
    this.message = message;
    this.all = Optional.ofNullable(all);
    this.only = Optional.ofNullable(only).orElse(ImmutableList.of());
    this.amend = Optional.ofNullable(amend);
    this.allowEmpty = Optional.ofNullable(allowEmpty);
    this.noVerify = Optional.ofNullable(noVerify);
  }

  @Builder(toBuilder = true)
  private GitCommitCommand(@NonNull final String message,
                           @NonNull final Optional<Boolean> all,
                           @NonNull final List<String> only,
                           @NonNull final Optional<Boolean> amend,
                           @NonNull final Optional<Boolean> allowEmpty,
                           @NonNull final Optional<Boolean> noVerify) {
    this.message = message;
    this.all = all;
    this.only = only;
    this.amend = amend;
    this.allowEmpty = allowEmpty;
    this.noVerify = noVerify;
  }
}
