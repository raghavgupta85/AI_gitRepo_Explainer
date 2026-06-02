package com.example.repoexplainer.util;

import java.util.HashMap;
import java.util.Map;

public class AIResponseParser {

    public static Map<String, String> parse(
            String response
    ) {

        Map<String, String> result =
                new HashMap<>();

        response =
                response.replace("**", "")
                        .replace("##", "")
                        .trim();

        result.put(
                "summary",
                extractSection(
                        response,
                        "SUMMARY:"
                )
        );

        result.put(
                "techStack",
                extractSection(
                        response,
                        "TECH_STACK:"
                )
        );

        result.put(
                "architecture",
                extractSection(
                        response,
                        "ARCHITECTURE:"
                )
        );

        result.put(
                "setup",
                extractSection(
                        response,
                        "SETUP:"
                )
        );

        result.put(
                "beginnerExplanation",
                extractSection(
                        response,
                        "BEGINNER_EXPLANATION:"
                )
        );

        return result;
    }

    private static String extractSection(

            String text,

            String section
    ) {

        int start =
                text.indexOf(section);

        if (start == -1) {

            return "";
        }

        start += section.length();

        int end =
                text.length();

        String[] sections = {

                "SUMMARY:",

                "TECH_STACK:",

                "ARCHITECTURE:",

                "SETUP:",

                "BEGINNER_EXPLANATION:"
        };

        for (String nextSection : sections) {

            if (nextSection.equals(section)) {
                continue;
            }

            int nextIndex =
                    text.indexOf(
                            nextSection,
                            start
                    );

            if (
                    nextIndex != -1
                            &&
                            nextIndex < end
            ) {

                end = nextIndex;
            }
        }

        return text.substring(start, end)
                .trim();
    }
}
