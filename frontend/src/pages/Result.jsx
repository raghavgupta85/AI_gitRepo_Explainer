import { useEffect, useState } from "react";

import { useLocation } from "react-router-dom";

import ChatWindow from "../components/ChatWindow";

import useTheme from "../hooks/useTheme";

import jsPDF from "jspdf";

import FileTreeViewer from "../components/FileTreeViewer";

function Result() {

    const {
        darkMode,
        setDarkMode
    } = useTheme();

    const location = useLocation();

    const repoUrl =
        location.state?.repoUrl || "";

    const [result, setResult] =
        useState(null);

    const [loading, setLoading] =
        useState(true);

    useEffect(() => {

        async function analyzeRepository() {

            try {

                const response =
                    await fetch(
                        "http://localhost:8080/api/repo/analyze",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            body: JSON.stringify({
                                repoUrl
                            })
                        }
                    );

                const data =
                    await response.json();

                setResult(data);

            } catch (error) {

                console.log(error);

            } finally {

                setLoading(false);
            }
        }

        analyzeRepository();

    }, [repoUrl]);

    function downloadPDF() {

    const doc = new jsPDF();

    const pageWidth = 170;

    let y = 20;

    function addSection(title, content) {

        doc.setFontSize(18);

        doc.text(title, 20, y);

        y += 10;

        doc.setFontSize(12);

        const lines =
            doc.splitTextToSize(
                content,
                pageWidth
            );

        doc.text(lines, 20, y);

        y += lines.length * 7 + 15;

        if (y > 250) {

            doc.addPage();

            y = 20;
        }
    }

    doc.setFontSize(24);

    doc.text(
        "GitHub Repository Analysis",
        20,
        y
    );

    y += 20;

    addSection(
        "Summary",
        result.summary
    );

    addSection(
        "Tech Stack",
        result.techStack
    );

    addSection(
        "Architecture",
        result.architecture
    );

    addSection(
        "Setup Instructions",
        result.setupInstructions
    );

    addSection(
        "Beginner Explanation",
        result.beginnerExplanation
    );

    doc.save(
        "repository-analysis.pdf"
    );
}

    if (loading) {

        return (

            <div className="result-container">

                <button
                    className="theme-toggle"
                    onClick={() =>
                        setDarkMode(!darkMode)
                    }
                >

                    {
                        darkMode
                            ? "Light Mode"
                            : "Dark Mode"
                    }

                </button>

                <h1 className="loading-text">
                    Analyzing Repository...
                </h1>

            </div>
        );
    }

    return (

        <div className="result-container">

            <button
                className="theme-toggle"
                onClick={() =>
                    setDarkMode(!darkMode)
                }
            >

                {
                    darkMode
                        ? "Light Mode"
                        : "Dark Mode"
                }

            </button>

            <h1 className="main-title">
                Repository Analysis
            </h1>

            <button
                className="download-button"
                onClick={downloadPDF}
            >

                Download PDF Report

            </button>

            <div className="card">

                <h2>
                    Summary
                </h2>

                <p>
                    {result.summary}
                </p>

            </div>

            <div className="card">

                <h2>
                    Tech Stack
                </h2>

                <p>
                    {result.techStack}
                </p>

            </div>

            <div className="card">

                <h2>
                    Architecture
                </h2>

                <p>
                    {result.architecture}
                </p>

            </div>

            <div className="card">

                <h2>
                    Setup Instructions
                </h2>

                <p>
                    {result.setupInstructions}
                </p>

            </div>

            <div className="card">

                <h2>
                    Beginner Explanation
                </h2>

                <p>
                    {result.beginnerExplanation}
                </p>

            </div>

            <FileTreeViewer
                repoUrl={repoUrl}
            />

            <ChatWindow
                repoUrl={repoUrl}
            />

        </div>
    );
}

export default Result;