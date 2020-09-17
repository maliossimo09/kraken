package com.octoperf.kraken.git.service.cmd.parser;

import com.octoperf.kraken.git.entity.GitStatus;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface GitStatusParser extends Predicate<String>, BiConsumer<GitStatus.GitStatusBuilder, String> {

}
