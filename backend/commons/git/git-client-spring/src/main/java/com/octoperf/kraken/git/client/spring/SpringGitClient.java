package com.octoperf.kraken.git.client.spring;

import com.octoperf.kraken.git.client.api.GitClient;
import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class SpringGitClient implements GitClient {

  @NonNull GitProjectService service;
  @NonNull Owner owner;

  @Override
  public Mono<GitConfiguration> connect(final String repositoryUrl) {
    return this.service.connect(this.owner, repositoryUrl);
  }
}
