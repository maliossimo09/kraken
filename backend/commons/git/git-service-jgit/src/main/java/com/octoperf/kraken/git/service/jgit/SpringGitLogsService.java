package com.octoperf.kraken.git.service.jgit;

import com.octoperf.kraken.git.command.GitCommandLog;
import com.octoperf.kraken.git.service.api.GitLogsService;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.tools.log.AbstractLogService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class SpringGitLogsService extends AbstractLogService<GitCommandLog> implements GitLogsService {

  @Override
  public void add(Owner owner, String text) {
    this.add(GitCommandLog.builder().owner(owner).text(text).build());
  }

  @Override
  public Disposable push(Owner owner, Flux<String> logs) {
    return null;
  }
}
