import { useEffect, useState } from "react";

import { useNavigate } from "react-router-dom";

import useTheme from "../hooks/useTheme";

function Home() {

    const [repoUrl, setRepoUrl] =
        useState("");

    const [history, setHistory] =
        useState([]);

    const navigate = useNavigate();

    const {
        darkMode,
        setDarkMode
    } = useTheme();

    useEffect(() => {

        const savedRepos =
            JSON.parse(
                localStorage.getItem(
                    "repoHistory"
                )
            ) || [];

        setHistory(savedRepos);

    }, []);

    function handleAnalyze() {

        if (!repoUrl.trim()) {

            return;
        }

        const updatedHistory = [

            repoUrl,

            ...history.filter(
                (repo) =>
                    repo !== repoUrl
            )

        ].slice(0, 5);

        localStorage.setItem(

            "repoHistory",

            JSON.stringify(
                updatedHistory
            )
        );

        setHistory(updatedHistory);

        navigate(
            "/result",
            {
                state: {
                    repoUrl
                }
            }
        );
    }

    function handleKeyDown(event) {

        if (event.key === "Enter") {

            handleAnalyze();
        }
    }

    return (

        <div className="home-container">

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
                GitHub Repo Explainer
            </h1>

            <input
                type="text"
                placeholder="Enter GitHub Repository URL"
                value={repoUrl}
                onChange={(e) =>
                    setRepoUrl(
                        e.target.value
                    )
                }
                onKeyDown={handleKeyDown}
                className="repo-input"
            />

            <button
                onClick={handleAnalyze}
                className="analyze-button"
            >
                Analyze Repository
            </button>

            <div className="history-container">

                <h2>
                    Recent Repositories
                </h2>

                {
                    history.length === 0
                    &&
                    (
                        <p>
                            No repositories yet
                        </p>
                    )
                }

                {
                    history.map(
                        (repo, index) => (

                            <div
                                key={index}
                                className="history-item"
                                onClick={() =>
                                    setRepoUrl(repo)
                                }
                            >

                                {repo}

                            </div>
                        )
                    )
                }

            </div>

        </div>
    );
}

export default Home;