package com.octoperf.kraken.git.service.jgit;

import com.google.common.collect.ImmutableList;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.eclipse.jgit.pgm.Main;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JGitCommandLineService extends Main {

  @NonNull OwnerToPath ownerToPath;

  public Mono<Void> execute(final Owner owner, final String command) throws Exception {
    final var path = this.ownerToPath.apply(owner);
    // TODO Fail if "--ssh" or "--git-dir" is set
    // TODO Force git-dir to user path
    final var args = CommandLineUtils.translateCommandline(command);
    super.run(ImmutableList.<String>builder()
        .add("--git-dir")
        .add(path.resolve(".git").toString())
        .addAll(Arrays.asList(args))
        .build().toArray(new String[0]));

    return null;
  }

}
