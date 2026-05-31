package dev.jlynx.atiperatask;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

record BranchData(
        String branchName,
        String lastCommitSha
) {

    @JsonCreator
    static BranchData fromJson(
            @JsonProperty("name")
            String branchName,
            @JsonProperty("commit")
            Map<String, Object> commit
    ) {
        String lastCommitSha = (commit != null && commit.containsKey("sha"))
                ? (String) commit.get("sha")
                : null;
        return new BranchData(branchName, lastCommitSha);
    }
}
