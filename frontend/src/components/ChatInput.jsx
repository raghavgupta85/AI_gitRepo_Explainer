import { useState } from "react";

function ChatInput({ onSend }) {

    const [question, setQuestion] =
        useState("");

    function handleSend() {

        onSend(question);

        setQuestion("");
    }

    function handleKeyDown(event) {

        if (event.key === "Enter") {

            handleSend();
        }
    }

    return (

        <div className="chat-input-container">

            <input
                type="text"
                placeholder="Ask a question..."
                value={question}
                onChange={(e) =>
                    setQuestion(e.target.value)
                }
                onKeyDown={handleKeyDown}
                className="chat-input"
            />

            <button
                onClick={handleSend}
                className="send-button"
            >
                Send
            </button>

        </div>
    );
}

export default ChatInput;