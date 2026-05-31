import { useState } from "react";

import API from "../services/api";

function Chatbot() {

  const [repoUrl, setRepoUrl] =
    useState("");

  const [question, setQuestion] =
    useState("");

  const [messages, setMessages] =
    useState([]);

  const [loading, setLoading] =
    useState(false);

  const [mode, setMode] =
    useState("repo");

  async function sendMessage() {

    if (!question.trim()) {
      return;
    }

    const userMessage = {
      type: "user",
      text: question
    };

    setMessages(prev => [
      ...prev,
      userMessage
    ]);

    setLoading(true);

    try {

      const response =
        await API.post(
          "/repo/chat",
          {
            repoUrl,
            question,
            mode
          }
        );

      const botMessage = {
        type: "bot",
        text: response.data.answer
      };

      setMessages(prev => [
        ...prev,
        botMessage
      ]);

    } catch (error) {

      console.error(error);

      alert("Chat failed");

    } finally {

      setLoading(false);

      setQuestion("");
    }
  }

  return (

    <div className="chat-container">

      <h1>
        Repository AI Chatbot
      </h1>

      <input
        type="text"
        placeholder="GitHub Repository URL"
        value={repoUrl}
        onChange={
          (e) =>
            setRepoUrl(e.target.value)
        }
      />

      <div className="mode-buttons">

        <button
          className={
            mode === "repo"
              ? "active-mode"
              : ""
          }
          onClick={() => setMode("repo")}
        >
          Repository Mode
        </button>

        <button
          className={
            mode === "general"
              ? "active-mode"
              : ""
          }
          onClick={() => setMode("general")}
        >
          General AI Mode
        </button>

      </div>

      <div className="chat-box">

        {
          messages.map(
            (msg, index) => (

              <div
                key={index}
                className={
                  msg.type === "user"
                    ? "user-message"
                    : "bot-message"
                }
              >

                {msg.text}

              </div>
            )
          )
        }

      </div>

      <div className="chat-input-area">

        <input
          type="text"
          placeholder="Ask a question..."
          value={question}
          onChange={
            (e) =>
              setQuestion(e.target.value)
          }
        />

        <button
          onClick={sendMessage}
        >

          {
            loading
              ? "Thinking..."
              : "Send"
          }

        </button>

      </div>

    </div>
  );
}

export default Chatbot;