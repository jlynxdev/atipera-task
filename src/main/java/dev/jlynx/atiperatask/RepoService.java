package dev.jlynx.atiperatask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
class RepoService {

    private final Executor githubExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final GitHubClient github;

    @Autowired
    public RepoService(GitHubClient github) {
        this.github = github;
    }

    public List<Repo> getReposByUser(Optional<String> username) {
        String user = username.orElseThrow(() -> new UsernameNotFoundException(""));
        List<RepoData> repos = github.getReposDataByUser(user);
        List<CompletableFuture<Repo>> futures = repos.stream()
                .filter(repoData -> !repoData.fork())
                .map(repo -> CompletableFuture.supplyAsync(() -> {
                    List<BranchData> branches = github.getBranchesDataByRepo(repo.repoName(), user);
                    return new Repo(repo, branches);
                }, githubExecutor))
                .toList();
        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}
