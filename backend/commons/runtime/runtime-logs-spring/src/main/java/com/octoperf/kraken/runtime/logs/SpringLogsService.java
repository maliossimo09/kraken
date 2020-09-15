package com.octoperf.kraken.runtime.logs;

import com.octoperf.kraken.runtime.entity.log.Log;
import com.octoperf.kraken.runtime.entity.log.LogStatus;
import com.octoperf.kraken.runtime.entity.log.LogType;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.tools.log.AbstractLogService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class SpringLogsService extends AbstractLogService<Log> implements LogsService {

  private static final int MAX_LOGS_SIZE = 500;
  private static final Duration MAX_LOGS_TIMEOUT_MS = Duration.ofMillis(1000);
  private static final String LINE_SEP = "\r\n";

  ConcurrentMap<String, Disposable> subscriptions = new ConcurrentHashMap<>();

  @Override
  public boolean dispose(final Owner owner, final String id, final LogType type) {
    if (this.subscriptions.containsKey(id)) {
      this.add(Log.builder().owner(owner).id(id).type(type).text("").status(LogStatus.CLOSED).build());
      final var subscription = this.subscriptions.get(id);
      subscription.dispose();
      return true;
    }
    return false;
  }

  @Override
  public Disposable push(final Owner owner, final String id, final LogType type, final Flux<String> stringFlux) {
    final var subscription = stringFlux
        .windowTimeout(MAX_LOGS_SIZE, MAX_LOGS_TIMEOUT_MS)
        .flatMap(window -> window.reduce((o, o2) -> o + LINE_SEP + o2))
        .map(text -> Log.builder().owner(owner).id(id).type(type).text(text).status(LogStatus.RUNNING).build())
        .concatWith(Flux.just(Log.builder().owner(owner).id(id).type(type).text("").status(LogStatus.CLOSED).build()))
        .doOnTerminate(() -> subscriptions.remove(id))
        .subscribeOn(Schedulers.elastic())
        .subscribe(this::add);
    this.subscriptions.put(id, subscription);
    return subscription;
  }

  @Override
  public void clear() {
    super.clear();
    subscriptions.values().forEach(Disposable::dispose);
    subscriptions.clear();
  }
}
