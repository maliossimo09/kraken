package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.security.entity.owner.Owner;
import org.eclipse.jgit.api.TransportConfigCallback;
import reactor.core.publisher.Mono;

import java.util.function.Function;

interface OwnerToTransportConfig extends Function<Owner, Mono<TransportConfigCallback>> {
}
