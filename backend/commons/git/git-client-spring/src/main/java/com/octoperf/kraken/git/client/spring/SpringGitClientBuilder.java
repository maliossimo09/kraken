package com.octoperf.kraken.git.client.spring;

import com.octoperf.kraken.git.client.api.GitClient;
import com.octoperf.kraken.git.client.api.GitClientBuilder;
import com.octoperf.kraken.git.service.api.GitFileServiceBuilder;
import com.octoperf.kraken.git.service.api.GitProjectService;
import com.octoperf.kraken.security.authentication.api.UserProviderFactory;
import com.octoperf.kraken.security.authentication.client.api.AuthenticatedClientBuildOrder;
import com.octoperf.kraken.security.authentication.client.spring.SpringAuthenticatedClientBuilder;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class SpringGitClientBuilder extends SpringAuthenticatedClientBuilder<GitClient> implements GitClientBuilder {

  GitProjectService projectService;
  GitFileServiceBuilder fileServiceBuilder;

  public SpringGitClientBuilder(final List<UserProviderFactory> userProviderFactories,
                                @NonNull final GitProjectService projectService,
                                @NonNull final GitFileServiceBuilder fileServiceBuilder) {
    super(userProviderFactories);
    this.projectService = projectService;
    this.fileServiceBuilder = fileServiceBuilder;
  }

  @Override
  public Mono<GitClient> build(final AuthenticatedClientBuildOrder order) {
    return super.getOwner(order).flatMap(owner -> this.fileServiceBuilder.build(owner)
        .map(fileService -> new SpringGitClient(projectService, fileService, owner)));
  }
}
