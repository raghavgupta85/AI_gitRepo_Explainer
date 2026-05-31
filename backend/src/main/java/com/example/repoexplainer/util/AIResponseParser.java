package com.example.repoexplainer.util;

import java.util.HashMap;
import java.util.Map;

public class AIResponseParser {

    public static Map<String, String> parse(
            String text
    ) {

        Map<String, String> result =
                new HashMap<>();

        String currentKey = "";

        StringBuilder currentValue =
                new StringBuilder();

        String[] lines =
                text.split("\n");

        for (String line : lines) {

            String upper =
                    line.toUpperCase();

            if (upper.startsWith("SUMMARY")) {

                save(
                        result,
                        currentKey,
                        currentValue
                );

                currentKey = "summary";

                currentValue =
                        new StringBuilder();
            }

            else if (
                    upper.startsWith(
                            "TECH_STACK"
                    )
            ) {

                save(
                        result,
                        currentKey,
                        currentValue
                );

                currentKey = "techStack";

                currentValue =
                        new StringBuilder();
            }

            else if (
                    upper.startsWith(
                            "ARCHITECTURE"
                    )
            ) {

                save(
                        result,
                        currentKey,
                        currentValue
                );

                currentKey =
                        "architecture";

                currentValue =
                        new StringBuilder();
            }

            else if (
                    upper.startsWith("SETUP")
            ) {

                save(
                        result,
                        currentKey,
                        currentValue
                );

                currentKey =
                        "setup";

                currentValue =
                        new StringBuilder();
            }

            else if (
                    upper.startsWith(
                            "BEGINNER_EXPLANATION"
                    )
            ) {

                save(
                        result,
                        currentKey,
                        currentValue
                );

                currentKey =
                        "beginnerExplanation";

                currentValue =
                        new StringBuilder();
            }

            else {

                currentValue
                        .append(line)
                        .append("\n");
            }
        }

        save(
                result,
                currentKey,
                currentValue
        );

        return result;
    }

    private static void save(
            Map<String, String> map,
            String key,
            StringBuilder value
    ) {

        if (!key.isEmpty()) {

            map.put(
                    key,
                    value.toString().trim()
            );
        }
    }
}