package dev.jlynx.atiperatask;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

record Repo(
        String repoName,
        String ownerLogin,
        List<BranchData> branches
) {

    Repo(RepoData repoData, List<BranchData> branches) {
        this(repoData.repoName(), repoData.ownerLogin(), branches);
    }

    @JsonCreator
    static Repo fromJson(
            @JsonProperty("repoName")
            String repoName,
            @JsonProperty("ownerLogin")
            String ownerLogin,
            @JsonProperty("branches")
            List<Map<String, String>> branches
    ) {
        List<BranchData> branchData = new ArrayList<>();
        branches.forEach(b -> branchData.add(new BranchData(b.get("name"), b.get("lastCommitSha"))));
        return new Repo(repoName, ownerLogin, branchData);
    }
}
