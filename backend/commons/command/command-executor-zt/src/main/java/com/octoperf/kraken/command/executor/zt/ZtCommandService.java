package com.octoperf.kraken.command.executor.zt;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.command.entity.Command;
import com.octoperf.kraken.command.executor.api.CommandService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
final class ZtCommandService implements CommandService {

  @NonNull
  UnaryOperator<String> stringCleaner;

  @Override
  public Flux<String> execute(final Command command) {
    return Flux.<String>create(emitter -> {
      log.debug(String.format("Executing command %s in path %s", String.join(" ", command.getArgs()), command.getPath()));
      final var file = Paths.get(command.getPath()).toFile();
      final var errors = ImmutableList.<String>builder();
      final var process = new ProcessExecutor()
          .exitValueNormal()
          .command(command.getArgs())
          .directory(file)
          .environment(command.getEnvironment().entrySet().stream()
              .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue)))
          .redirectErrorStream(true)
          .redirectOutput(new LogOutputStream() {
            @Override
            protected void processLine(final String line) {
              errors.add(line);
              emitter.next(line);
            }
          });
      try {
        process.execute();
      } catch (InvalidExitValueException | InterruptedException | TimeoutException | IOException e) {
        log.error("Command execution failed", e);
        log.error(String.join("\n", errors.build()));
        emitter.error(e);
        // Restore interrupted state...
        Thread.currentThread().interrupt();
      }
      emitter.complete();
    })
        .map(stringCleaner);
  }

  @Override
  public Mono<List<String>> parseCommandLine(final String commandLine) {
    return Mono.fromCallable(() -> CommandLineUtils.translateCommandline(commandLine)).map(Arrays::asList);
  }

  @Override
  public Mono<Command> validate(Command command) {
    return Mono.fromCallable(() -> {
      command.getArgs().forEach(this::checkArg);
      return command;
    });
  }

  private void checkArg(final String arg) {
    checkArgument(!arg.contains(".."), "Argument cannot contain '..'");
    checkArgument(!arg.contains("&&"), "Argument cannot contain '&&'");
    checkArgument(!arg.contains("|"), "Argument cannot contain '|'");
    checkArgument(!arg.contains(">"), "Argument cannot contain '>'");
    checkArgument(!arg.startsWith("/"), "Argument cannot start with '/'");
  }
}
