package com.octoperf.kraken.git.credentials.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import com.octoperf.kraken.git.credentials.api.GitCredentialsService;
import com.octoperf.kraken.git.entity.GitCredentials;
import com.octoperf.kraken.security.authentication.api.AuthenticationMode;
import com.octoperf.kraken.security.authentication.client.api.AuthenticatedClientBuildOrder;
import com.octoperf.kraken.security.entity.owner.Owner;
import com.octoperf.kraken.storage.client.api.StorageClientBuilder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JschGitCredentialsService implements GitCredentialsService {

  @NonNull
  StorageClientBuilder storageClientBuilder;

  @Override
  public Mono<GitCredentials> getCredentials(final Owner owner) {
    return this.storageClientBuilder.build(AuthenticatedClientBuildOrder.builder().mode(AuthenticationMode.IMPERSONATE).userId(owner.getUserId()).build())
        .flatMap(client -> Mono.zip(client.getContent(".ssh/id_rsa"), client.getContent(".ssh/id_rsa.pub")))
        .map(t2 -> GitCredentials.builder().privateKey(t2.getT1()).publicKey(t2.getT2()).build());
  }

  @Override
  public Mono<GitCredentials> initCredentials(final String userId) {
    return Mono.zip(this.storageClientBuilder.build(AuthenticatedClientBuildOrder.builder().mode(AuthenticationMode.IMPERSONATE).userId(userId).build()), this.createCredentials(userId))
        .flatMap(t2 -> {
          final var client = t2.getT1();
          final var credentials = t2.getT2();
          // Save credentials user storage client
          return Mono.zip(client.setContent(".ssh/id_rsa", credentials.getPrivateKey()), client.setContent(".ssh/id_rsa.pub", credentials.getPublicKey()))
              .map(objects -> credentials);
        });
  }

  private Mono<GitCredentials> createCredentials(final String userId) {
    return Mono.fromCallable(() -> {
      // Create key pair
      final var jsch = new JSch();
      final var kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA);

      // Write the key pair into strings
      final var privateOS = new ByteArrayOutputStream();
      final var publicOS = new ByteArrayOutputStream();
      kpair.writePrivateKey(privateOS);
      kpair.writePublicKey(publicOS, userId);
      final var privateKey = new String(privateOS.toByteArray());
      final var publicKey = new String(publicOS.toByteArray());
      kpair.dispose();

      return GitCredentials.builder()
          .publicKey(publicKey)
          .privateKey(privateKey)
          .build();
    });
  }
}
