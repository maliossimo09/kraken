package com.octoperf.kraken.git.server.rest;

import com.octoperf.kraken.git.entity.GitLog;
import com.octoperf.kraken.git.entity.command.GitSubCommand;
import com.octoperf.kraken.git.service.api.GitFileService;
import com.octoperf.kraken.git.service.api.GitFileServiceBuilder;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Pattern;
import java.util.List;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Slf4j
@RestController
@RequestMapping("/git")
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GitFileController {

  @NonNull GitFileServiceBuilder fileServiceBuilder;
  @NonNull UserProvider userProvider;

  private Mono<GitFileService> gitFileService(final String applicationId, final String projectId) {
    return userProvider.getOwner(applicationId, projectId).flatMap(fileServiceBuilder::build);
  }

  @PostMapping("/execute")
  public Mono<Void> connect(@RequestHeader("ApplicationId") @Pattern(regexp = "[a-z0-9]*") final String applicationId,
                            @RequestHeader(name = "ProjectId") final String projectId,
                            @RequestBody() final GitSubCommand command) {
    return this.gitFileService(applicationId, projectId).flatMap(gitFileService -> gitFileService.execute(command).then(Mono.fromCallable(() -> {
      gitFileService.close();
      return null;
    })));
  }

  @GetMapping("/log")
  public Mono<List<GitLog>> log(@RequestHeader("ApplicationId") @Pattern(regexp = "[a-z0-9]*") final String applicationId,
                                @RequestHeader(name = "ProjectId") final String projectId,
                                @RequestParam(value = "path") final String path) {
    return this.gitFileService(applicationId, projectId).flatMap(gitFileService -> gitFileService.log(path));
  }

  @PostMapping(value = "/cat", produces = TEXT_PLAIN_VALUE)
  public Mono<String> cat(@RequestHeader("ApplicationId") @Pattern(regexp = "[a-z0-9]*") final String applicationId,
                          @RequestHeader(name = "ProjectId") final String projectId,
                          @RequestBody() final GitLog log) {
    return this.gitFileService(applicationId, projectId).flatMap(gitFileService -> gitFileService.cat(log));
  }
}
