package dev.jlynx.atiperatask;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWireMock(@ConfigureWireMock(baseUrlProperties = "github-client.base-url"))
class RepoIntegrationTests {

    private static class MockData {
        static final String username = "testuser";
        static final String repoName = "testrepo";
        static final String branchName = "main";
        static final String lastCommitSha = "sha1";

        static List<Map<String, Object>> getReposResponse() {
            Map<String, String> repoOwner = new HashMap<>();
            repoOwner.put("login", username);
            Map<String, Object> repo = new HashMap<>();
            repo.put("name", repoName);
            repo.put("owner", repoOwner);
            repo.put("fork", false);
            return List.of(repo);
        }

        static List<Map<String, Object>> getReposResponseWithForks() {
            Map<String, String> repoOwner = new HashMap<>();
            repoOwner.put("login", username);
            Map<String, Object> repo = new HashMap<>();
            repo.put("name", repoName);
            repo.put("owner", repoOwner);
            repo.put("fork", false);
            Map<String, Object> forkRepo = new HashMap<>();
            forkRepo.put("name", "forkedrepo");
            forkRepo.put("owner", repoOwner);
            forkRepo.put("fork", true);
            return List.of(forkRepo, repo);
        }

        static List<Map<String, Object>> getBranchesResponse() {
            Map<String, String> branchCommit = new HashMap<>();
            branchCommit.put("sha", lastCommitSha);
            Map<String, Object> branch = new HashMap<>();
            branch.put("name", branchName);
            branch.put("commit", branchCommit);
            return List.of(branch);
        }
    }

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnReposForUser() throws Exception {
        // given
        var mockReposResponse = MockData.getReposResponse();

        stubFor(WireMock.get(urlEqualTo("/users/" + MockData.username + "/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/vnd.github+json")
                        .withJsonBody(new ObjectMapper().valueToTree(mockReposResponse))));

        stubFor(WireMock.get(urlEqualTo("/repos/" + MockData.username + "/" + MockData.repoName + "/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/vnd.github+json")
                        .withJsonBody(jsonMapper.valueToTree(MockData.getBranchesResponse()))));

        // when
        String responseBodyJson = mockMvc.perform(get("/repo/" + MockData.username)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Repo> responseBody = jsonMapper.readValue(responseBodyJson, new TypeReference<>() {});

        // then
        assertThat(responseBody).hasSize(1);
        assertThat(responseBody.getFirst().repoName()).isEqualTo(MockData.repoName);
        assertThat(responseBody.getFirst().ownerLogin()).isEqualTo(MockData.username);
        assertThat(responseBody.getFirst().branches()).hasSize(1);
        assertThat(responseBody.getFirst().branches().getFirst().branchName())
                .isEqualTo(MockData.branchName);
        assertThat(responseBody.getFirst().branches().getFirst().lastCommitSha())
                .isEqualTo(MockData.lastCommitSha);
    }

    @Test
    void shouldFilterOutForks() throws Exception {
        // given
        var mockReposWithForksResponse = MockData.getReposResponseWithForks();

        stubFor(WireMock.get(urlEqualTo("/users/" + MockData.username + "/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/vnd.github+json")
                        .withJsonBody(new ObjectMapper().valueToTree(mockReposWithForksResponse))));

        stubFor(WireMock.get(urlEqualTo("/repos/" + MockData.username + "/" + MockData.repoName + "/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/vnd.github+json")
                        .withJsonBody(jsonMapper.valueToTree(MockData.getBranchesResponse()))));

        // when
        String responseBodyJson = mockMvc.perform(get("/repo/" + MockData.username)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Repo> responseBody = jsonMapper.readValue(responseBodyJson, new TypeReference<>() {});

        // then
        assertThat(responseBody).hasSize(1);
        assertThat(responseBody.getFirst().repoName()).isEqualTo(MockData.repoName);
        assertThat(responseBody.getFirst().ownerLogin()).isEqualTo(MockData.username);
        assertThat(responseBody.getFirst().branches()).hasSize(1);
        assertThat(responseBody.getFirst().branches().getFirst().branchName())
                .isEqualTo(MockData.branchName);
        assertThat(responseBody.getFirst().branches().getFirst().lastCommitSha())
                .isEqualTo(MockData.lastCommitSha);
    }

    @Test
    void shouldReturn404_WhenUsernameNotFound() throws Exception {
        // Given
        String username = "nonexistent";
        stubFor(WireMock.get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse().withStatus(404)));

        // when
        String responseBodyJson = mockMvc.perform(get("/repo/" + username)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ErrorResponse responseBody = jsonMapper.readValue(responseBodyJson, new TypeReference<>() {});

        // then
        assertThat(responseBody.status()).isEqualTo(404);
        assertThat(responseBody.message()).isEqualTo("GitHub username '" + username + "' not found.");
    }

    @ParameterizedTest
    @ValueSource(strings = { "/repo", "/repo/" })
    void shouldReturn404_WhenNoUsernameGiven(String path) throws Exception {
        // when
        String responseBodyJson = mockMvc.perform(get(path)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ErrorResponse responseBody = jsonMapper.readValue(responseBodyJson, new TypeReference<>() {});

        // then
        assertThat(responseBody.status()).isEqualTo(404);
        assertThat(responseBody.message()).isEqualTo("GitHub username '' not found.");
    }
}
