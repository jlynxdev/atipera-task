package dev.jlynx.atiperatask;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

record RepoData(
        String repoName,
        String ownerLogin,
        boolean fork
) {

    @JsonCreator
    static RepoData fromJson(
            @JsonProperty("name")
            String repoName,
            @JsonProperty("owner")
            Map<String, Object> owner,
            @JsonProperty("fork")
            boolean fork
    ) {
        String login = (owner != null && owner.containsKey("login"))
                ? (String) owner.get("login")
                : null;
        return new RepoData(repoName, login, fork);
    }
}
