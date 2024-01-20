import React, { ChangeEvent, useState } from 'react';

const ChatAndCommandFrame: React.FC = () => {
  const chatEndpoint = 'ws://localhost:8080/chatEndpoint/test';
  const clientWebsocket = new WebSocket(chatEndpoint);

  const [inputText, setInputText] = useState('');
  const [chatMessages, setChatMessages] = useState<string[]>([]);

  const handleInputChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    setInputText(e.target.value);
  };

  const handleSendMessage = () => {
    if (inputText.trim() !== '') {
      setChatMessages([...chatMessages, inputText]);
      setInputText('');

      sendChatMessage(inputText)
        .then(() => { console.log("Successfully Sent Message!") })
        .catch((err) => { console.log("Error Sending Message" + err) })
    }
  };

  clientWebsocket.onopen = () => {
    console.log("Opening Chat Endpoint");
    
  }

  async function sendChatMessage(chat: string) {
    const jsonMessage = {
      "username": "test",
      "context": '/',
      "content": chat
    }
    await clientWebsocket.send(JSON.stringify(jsonMessage))
  }

  return (
    <div>
      <div style={{ maxHeight: '200px', overflowY: 'auto', border: '1px solid #ccc', padding: '10px' }}>
        {/* Display Chat Messages */}
        {chatMessages.map((message, index) => (
          <div key={index}>{message}</div>
        ))}
      </div>
      <div>
        {/* Input Textarea */}
        <textarea
          spellCheck="false"
          value={inputText}
          onChange={handleInputChange}
          style={{ width: '100%', marginTop: '10px', resize: 'none' }}
        ></textarea>
      </div>
      <div>
        {/* Send Message Button */}
        <button onClick={handleSendMessage} style={{ marginTop: '10px' }}>
          Send Message
        </button>
      </div>
    </div>
  );
};

export default ChatAndCommandFrame;
