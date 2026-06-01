import { useState } from "react";

import ChatInput from "./ChatInput";

function ChatWindow({ repoUrl }) {

    const [messages, setMessages] =
        useState([]);

    const [mode, setMode] =
        useState("repo");

    async function sendMessage(question) {

        if (!question.trim()) {

            return;
        }

        const userMessage = {

            type: "user",

            text: question
        };

        setMessages((prev) => [
            ...prev,
            userMessage
        ]);

        try {

            const response =
                await fetch(
                    "http://localhost:8080/api/repo/chat",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({

                            repoUrl,

                            question,

                            repositoryMode:
                                mode === "repo"
                        })
                    }
                );

            const data =
                await response.text();

            const botMessage = {

                type: "bot",

                text: data
            };

            setMessages((prev) => [
                ...prev,
                botMessage
            ]);

        } catch (error) {

            console.log(error);

            const errorMessage = {

                type: "bot",

                text: "Error while contacting AI backend."
            };

            setMessages((prev) => [
                ...prev,
                errorMessage
            ]);
        }
    }

    return (

        <div className="chat-container">

            <h1 className="chat-title">
                Repository AI Chatbot
            </h1>

            <div className="mode-buttons">

                <button
                    className={
                        mode === "repo"
                            ? "active-mode"
                            : ""
                    }
                    onClick={() =>
                        setMode("repo")
                    }
                >
                    Repository Mode
                </button>

                <button
                    className={
                        mode === "general"
                            ? "active-mode"
                            : ""
                    }
                    onClick={() =>
                        setMode("general")
                    }
                >
                    General AI Mode
                </button>

            </div>

            <div className="messages-container">

                {
                    messages.map(
                        (message, index) => (

                            <div
                                key={index}
                                className={
                                    message.type === "user"
                                        ? "user-message"
                                        : "bot-message"
                                }
                            >
                                {message.text}
                            </div>
                        )
                    )
                }

            </div>

            <ChatInput
                onSend={sendMessage}
            />

        </div>
    );
}

export default ChatWindow;