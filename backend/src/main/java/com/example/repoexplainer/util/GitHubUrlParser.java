package com.example.repoexplainer.util;

public class GitHubUrlParser {

    public static String[] extractOwnerAndRepo(
            String repoUrl
    ) {

        String cleanedUrl = repoUrl
                .replace("https://github.com/", "")
                .replace(".git", "");

        return cleanedUrl.split("/");
    }
}