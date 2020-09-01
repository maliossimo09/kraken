package com.octoperf.kraken.git.server.rest;

import com.octoperf.kraken.git.entity.GitCredentials;
import com.octoperf.kraken.git.service.api.GitUserService;
import com.octoperf.kraken.security.authentication.api.UserProvider;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("/git/user")
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GitUserController {

  @NonNull GitUserService userService;
  @NonNull UserProvider userProvider;

  @GetMapping("/publicKey")
  public Mono<String> getPublicKey() {
    return userProvider.getOwner("", "")
        .flatMap(owner -> userService.getCredentials(owner.getUserId()))
        .map(GitCredentials::getPublicKey);
  }
}
