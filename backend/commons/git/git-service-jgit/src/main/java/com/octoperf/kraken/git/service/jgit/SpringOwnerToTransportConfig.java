package com.octoperf.kraken.git.service.jgit;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.octoperf.kraken.git.service.api.GitUserService;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.util.FS;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class SpringOwnerToTransportConfig implements OwnerToTransportConfig {

  @NonNull GitUserService userService;

  @Override
  public Mono<TransportConfigCallback> apply(final Owner owner) {
    return userService.getCredentials(owner.getUserId()).flatMap(gitCredentials -> Mono.fromCallable(() -> {
      final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
        @Override
        protected JSch createDefaultJSch(FS fs) throws JSchException {
          final var jsch = super.createDefaultJSch(fs);
          jsch.addIdentity(owner.getUserId(),
              gitCredentials.getPrivateKey().getBytes(StandardCharsets.UTF_8),
              gitCredentials.getPublicKey().getBytes(StandardCharsets.UTF_8),
              null);
          return jsch;
        }
      };

      return transport -> {
        final SshTransport sshTransport = (SshTransport) transport;
        sshTransport.setSshSessionFactory(sshSessionFactory);
      };
    }));
  }
}
