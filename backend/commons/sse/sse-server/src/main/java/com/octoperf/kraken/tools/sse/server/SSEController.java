package com.octoperf.kraken.tools.sse.server;

import com.google.common.collect.ImmutableMap;
import com.octoperf.kraken.git.client.api.GitClientBuilder;
import com.octoperf.kraken.runtime.client.api.RuntimeClientBuilder;
import com.octoperf.kraken.security.authentication.client.api.AuthenticatedClientBuildOrder;
import com.octoperf.kraken.storage.client.api.StorageClientBuilder;
import com.octoperf.kraken.tools.sse.SSEService;
import com.octoperf.kraken.tools.sse.SSEWrapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Pattern;

import static com.octoperf.kraken.security.authentication.api.AuthenticationMode.SESSION;

@Slf4j
@RestController()
@RequestMapping("/sse")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Validated
public class SSEController {

  @NonNull SSEService sse;
  @NonNull RuntimeClientBuilder runtimeClientBuilder;
  @NonNull StorageClientBuilder storageClientBuilder;
  @NonNull GitClientBuilder gitClientBuilder;

  @GetMapping(value = "/watch")
  public Flux<ServerSentEvent<SSEWrapper>> watch(@RequestHeader("ApplicationId") @Pattern(regexp = "[a-z0-9]*") final String applicationId,
                                                 @RequestHeader("ProjectId") @Pattern(regexp = "[a-z0-9]{10}") final String projectId) {
    final var order = AuthenticatedClientBuildOrder.builder()
        .mode(SESSION)
        .applicationId(applicationId)
        .projectId(projectId)
        .build();
    final var storageClient = storageClientBuilder.build(order);
    final var runtimeClient = runtimeClientBuilder.build(order);
    final var gitClient = gitClientBuilder.build(order);
    return Mono.zip(storageClient, runtimeClient, gitClient)
        .flatMapMany(clients ->
            sse.keepAlive(sse.merge(ImmutableMap.of(
                "NODE", clients.getT1().watch(),
                "LOG", clients.getT2().watchLogs(),
                "TASKS", clients.getT2().watchTasks(),
                "GIT_STATUS", clients.getT3().watchStatus(),
                "GIT_REFRESH", clients.getT3().watchRefresh()
            ))))
        .map(event -> {
          log.debug(event.toString());
          return event;
        });
  }
}
