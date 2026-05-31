package com.example.repoexplainer.util;

public class ReadmeCleaner {

    public static String clean(
            String text
    ) {

        text =
                text.replaceAll(
                        "!\\[.*?\\]\\(.*?\\)",
                        ""
                );

        text =
                text.replaceAll(
                        "\\[.*?\\]\\(.*?\\)",
                        ""
                );

        text =
                text.replaceAll(
                        "http\\S+",
                        ""
                );

        text =
                text.replaceAll(
                        "#+",
                        ""
                );

        text =
                text.replaceAll(
                        "`",
                        ""
                );

        text =
                text.replaceAll(
                        "\\*",
                        ""
                );

        text =
                text.replaceAll(
                        "\\|",
                        ""
                );

        text =
                text.replaceAll(
                        ">",
                        ""
                );

        text =
                text.replaceAll(
                        "\\s+",
                        " "
                );

        return text.trim();
    }
}