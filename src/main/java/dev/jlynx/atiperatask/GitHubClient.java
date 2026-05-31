package dev.jlynx.atiperatask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
class GitHubClient {

    private final RestClient client;

    @Autowired
    GitHubClient(@Qualifier("githubClient") RestClient client) {
        this.client = client;
    }

    List<RepoData> getReposDataByUser(String username) {
        return client.get()
                .uri("/users/{username}/repos", username)
                .accept(MediaType.valueOf("application/vnd.github+json"))
                .retrieve()
                .onStatus(status -> status.value() == 404, (_, _) -> {
                    throw new UsernameNotFoundException(username);
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }

    List<BranchData> getBranchesDataByRepo(String repoName, String username) {
        return client.get()
                .uri("/repos/{username}/{repoName}/branches", username, repoName)
                .accept(MediaType.valueOf("application/vnd.github+json"))
                .retrieve()
                .onStatus(status -> status.value() == 404, (_, _) -> {
                    throw new UsernameNotFoundException(username + "/" + repoName);
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
