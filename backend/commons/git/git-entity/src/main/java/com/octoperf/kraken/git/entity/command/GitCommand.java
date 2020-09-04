package com.octoperf.kraken.git.entity.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GitAddCommand.class, name = "add"),
    @JsonSubTypes.Type(value = GitCommitCommand.class, name = "commit"),
    @JsonSubTypes.Type(value = GitRmCommand.class, name = "rm"),
    @JsonSubTypes.Type(value = GitFetchCommand.class, name = "fetch"),
    @JsonSubTypes.Type(value = GitMergeCommand.class, name = "merge"),
    @JsonSubTypes.Type(value = GitPullCommand.class, name = "pull"),
    @JsonSubTypes.Type(value = GitPushCommand.class, name = "push"),
    @JsonSubTypes.Type(value = GitResetCommand.class, name = "reset"),
    @JsonSubTypes.Type(value = GitRebaseCommand.class, name = "rebase"),
})
public interface GitCommand {
}
