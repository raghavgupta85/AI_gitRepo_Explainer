import { useEffect, useState } from "react";

function TreeNode({

    node,

    openFile

}) {

    const [expanded, setExpanded] =
        useState(false);

    const isFolder =
        node.type === "dir";

    return (

        <div className="tree-node">

            <div
                className="tree-item"

                onClick={() => {

                    if (isFolder) {

                        setExpanded(
                            !expanded
                        );

                    } else {

                        openFile(
                            node.path
                        );
                    }
                }}
            >

                <span>

                    {
                        isFolder
                            ? (
                                expanded
                                    ? "📂"
                                    : "📁"
                            )
                            : "📄"
                    }

                </span>

                <span className="tree-path">

                    {node.name}

                </span>

            </div>

            {
                expanded
                &&
                node.children
                &&
                (
                    <div className="tree-children">

                        {
                            node.children.map(
                                (
                                    child,
                                    index
                                ) => (

                                    <TreeNode
                                        key={index}

                                        node={child}

                                        openFile={
                                            openFile
                                        }
                                    />
                                )
                            )
                        }

                    </div>
                )
            }

        </div>
    );
}

function FileTreeViewer({

    repoUrl

}) {

    const [files, setFiles] =
        useState([]);

    const [loading, setLoading] =
        useState(true);

    const [selectedFile, setSelectedFile] =
        useState("");

    const [fileContent, setFileContent] =
        useState("");

    const [fileExplanation, setFileExplanation] =
        useState("");

    const [explaining, setExplaining] =
        useState(false);

    useEffect(() => {

        async function fetchFiles() {

            try {

                const response =
                    await fetch(
                        "http://localhost:8080/api/repo/files",
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

                setFiles(data);

            } catch (error) {

                console.log(error);

            } finally {

                setLoading(false);
            }
        }

        fetchFiles();

    }, [repoUrl]);

    async function openFile(
        filePath
    ) {

        try {

            setSelectedFile(filePath);

            setFileContent(
                "Loading file..."
            );

            setFileExplanation("");

            const response =
                await fetch(
                    "http://localhost:8080/api/repo/file-content",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            repoUrl,
                            filePath
                        })
                    }
                );

            const data =
                await response.text();

            setFileContent(data);

        } catch (error) {

            console.log(error);

            setFileContent(
                "Unable to load file."
            );
        }
    }

    async function explainSelectedFile() {

        try {

            setExplaining(true);

            setFileExplanation(
                "Generating AI explanation..."
            );

            const response =
                await fetch(
                    "http://localhost:8080/api/repo/explain-file",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            repoUrl,
                            filePath: selectedFile
                        })
                    }
                );

            const data =
                await response.text();

            setFileExplanation(data);

        } catch (error) {

            console.log(error);

            setFileExplanation(
                "Unable to explain file."
            );

        } finally {

            setExplaining(false);
        }
    }

    if (loading) {

        return (

            <div className="tree-container">

                <h2>
                    Loading File Tree...
                </h2>

            </div>
        );
    }

    return (

        <div className="tree-container">

            <h2>
                Repository File Tree
            </h2>

            <div className="tree-list">

                {
                    files.map(
                        (node, index) => (

                            <TreeNode
                                key={index}

                                node={node}

                                openFile={openFile}
                            />
                        )
                    )
                }

            </div>

            {
                selectedFile && (

                    <div className="file-preview">

                        <h2>

                            {selectedFile}

                        </h2>

                        <pre>

                            {fileContent}

                        </pre>

                        <button
                            className="explain-button"

                            onClick={
                                explainSelectedFile
                            }

                            disabled={explaining}
                        >

                            {
                                explaining
                                    ? "Explaining..."
                                    : "✨ Explain This File"
                            }

                        </button>

                        {
                            fileExplanation && (

                                <div className="file-explanation">

                                    <h3>

                                        AI Explanation

                                    </h3>

                                    <p>

                                        {fileExplanation}

                                    </p>

                                </div>
                            )
                        }

                    </div>
                )
            }

        </div>
    );
}

export default FileTreeViewer;