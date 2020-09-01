package com.octoperf.kraken.git.client.api;

import com.octoperf.kraken.git.entity.GitConfiguration;
import com.octoperf.kraken.security.authentication.client.api.AuthenticatedClient;
import reactor.core.publisher.Mono;

public interface GitClient extends AuthenticatedClient {

  Mono<GitConfiguration> connect(String repositoryUrl);

}
