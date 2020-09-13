package com.octoperf.kraken.git.service.jgit;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.octoperf.kraken.git.service.api.GitUserService;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.util.FS;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
final class KrakenUserGitCredentialsProvider extends JschConfigSessionFactory {

  @NonNull UserProvider userProvider;
  @NonNull GitUserService userService;

  // TODO Unit tests

  @PostConstruct
  public void post(){
    SshSessionFactory.setInstance(this);
  }

  @Override
  protected JSch createDefaultJSch(FS fs) throws JSchException {
    final var userId = Optional.ofNullable(userProvider.getAuthenticatedUser().block()).orElseThrow().getUserId();
    final var credentials = Optional.ofNullable(userService.getCredentials(userId).block()).orElseThrow();
    final var jsch = super.createDefaultJSch(fs);
    jsch.addIdentity(userId,
        credentials.getPrivateKey().getBytes(StandardCharsets.UTF_8),
        credentials.getPublicKey().getBytes(StandardCharsets.UTF_8),
        null);
    return jsch;
  }
}
