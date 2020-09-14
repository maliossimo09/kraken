package com.octoperf.kraken.git.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GitAddSubCommand.class, name = "add"),
    @JsonSubTypes.Type(value = GitCommitSubCommand.class, name = "commit"),
    @JsonSubTypes.Type(value = GitRmSubCommand.class, name = "rm"),
    @JsonSubTypes.Type(value = GitFetchSubCommand.class, name = "fetch"),
    @JsonSubTypes.Type(value = GitMergeSubCommand.class, name = "merge"),
    @JsonSubTypes.Type(value = GitPullSubCommand.class, name = "pull"),
    @JsonSubTypes.Type(value = GitPushSubCommand.class, name = "push"),
    @JsonSubTypes.Type(value = GitResetSubCommand.class, name = "reset"),
    @JsonSubTypes.Type(value = GitRebaseSubCommand.class, name = "rebase"),
})
public interface GitSubCommand {
}
