package com.octoperf.kraken.git.client.spring;

import com.octoperf.kraken.git.client.api.GitClient;
import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.git.entity.GitStatus;
import com.octoperf.kraken.git.event.GitRefreshStorageEvent;
import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.git.service.api.GitService;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class SpringGitClient implements GitClient {

  @NonNull GitProjectService projectService;
  @NonNull GitService gitService;
  @NonNull Owner owner;

  @Override
  public Mono<GitConfiguration> connect(final String repositoryUrl) {
    return this.projectService.connect(this.owner, repositoryUrl);
  }

  @Override
  public Flux<GitStatus> watchStatus() {
    return this.gitService.watchStatus(owner);
  }

  @Override
  public Flux<GitRefreshStorageEvent> watchRefresh() {
    return this.gitService.watchRefresh(owner);
  }
}
