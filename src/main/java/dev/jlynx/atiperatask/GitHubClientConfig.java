package dev.jlynx.atiperatask;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
class GitHubClientConfig {

    @Bean
    RestClient githubClient(
            @Value("${github-client.api-version}") String apiVersion,
            @Value("${github-client.base-url}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-GitHub-Api-Version", apiVersion)
                .build();
    }
}
