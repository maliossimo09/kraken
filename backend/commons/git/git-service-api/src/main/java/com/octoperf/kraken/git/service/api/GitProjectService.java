package com.octoperf.kraken.git.service.api;

import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.project.entity.Project;
import com.octoperf.kraken.security.entity.owner.Owner;
import reactor.core.publisher.Mono;

public interface GitProjectService {

  Mono<GitConfiguration> connect(Owner owner, String repositoryUrl);

  Mono<Void> disconnect(Owner owner, GitConfiguration git);

  Mono<Project> importFromRepository(Owner owner, String repositoryUrl);
}
