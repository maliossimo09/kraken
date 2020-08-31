package com.octoperf.kraken.git.credentials.api;

import com.octoperf.kraken.git.entity.GitCredentials;
import com.octoperf.kraken.security.entity.owner.Owner;
import reactor.core.publisher.Mono;

public interface GitCredentialsService {

  Mono<GitCredentials> getCredentials(Owner owner);

  Mono<GitCredentials> initCredentials(String userId);
}
