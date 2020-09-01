package com.octoperf.kraken.git.service.jgit;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import com.octoperf.kraken.git.entity.GitCredentials;
import com.octoperf.kraken.git.service.api.GitUserService;
import com.octoperf.kraken.security.entity.owner.Owner;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.octoperf.kraken.security.entity.owner.OwnerType.USER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JschGitUserService implements GitUserService {

  private static final String ID_RSA = "id_rsa";
  private static final String ID_RSA_PUB = "id_rsa.pub";

  @NonNull OwnerToPath ownerToPath;

  @Override
  public Mono<GitCredentials> getCredentials(final String userId) {
    return Mono.fromCallable(() -> {
      final var path = this.userIdToPath(userId);
      final var privateKey = Files.readString(path.resolve(ID_RSA), UTF_8);
      final var publicKey = Files.readString(path.resolve(ID_RSA_PUB), UTF_8);
      return GitCredentials.builder().privateKey(privateKey).publicKey(publicKey).build();
    });
  }

  @Override
  public Mono<GitCredentials> initCredentials(final String userId) {
    return Mono.fromCallable(() -> {
      final var path = this.userIdToPath(userId);
      final var jsch = new JSch();
      final var keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
      keyPair.writePrivateKey(path.resolve(ID_RSA).toString());
      keyPair.writePublicKey(path.resolve(ID_RSA_PUB).toString(), userId);
      log.info("Initialized SSH, Finger print: " + keyPair.getFingerPrint());
      keyPair.dispose();
      return null;
    }).flatMap(o -> this.getCredentials(userId));
  }

  @Override
  public Mono<Void> removeCredentials(final String userId) {
    return Mono.fromCallable(() -> {
      FileSystemUtils.deleteRecursively(this.userIdToPath(userId));
      return null;
    });
  }

  private Path userIdToPath(final String userId) {
    return this.ownerToPath.apply(Owner.builder().userId(userId).type(USER).build()).resolve(".ssh");
  }

}
