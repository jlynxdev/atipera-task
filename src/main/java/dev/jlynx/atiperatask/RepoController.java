package dev.jlynx.atiperatask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/repo")
class RepoController {

    private final RepoService repoService;

    @Autowired
    RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

    @GetMapping(value = {"", "/", "/{user}"})
    ResponseEntity<List<Repo>> getRepos(@PathVariable(required = false) Optional<String> user) {
        List<Repo> responseBody = repoService.getReposByUser(user);
        return ResponseEntity.ok(responseBody);
    }
}
