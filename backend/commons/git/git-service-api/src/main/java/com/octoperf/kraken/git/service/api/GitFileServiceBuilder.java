package com.octoperf.kraken.git.service.api;

import com.octoperf.kraken.security.entity.owner.Owner;
import reactor.core.publisher.Mono;

public interface GitFileServiceBuilder {

  Mono<GitFileService> build(Owner owner);

}
