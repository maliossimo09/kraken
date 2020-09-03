package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GitAddCommand.class, name = "add"),
    @JsonSubTypes.Type(value = GitCommitCommand.class, name = "commit"),
    @JsonSubTypes.Type(value = GitRmCommand.class, name = "rm")
})
public interface GitCommand {
}
