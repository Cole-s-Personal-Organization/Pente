import React, { ChangeEvent, useState, useEffect } from 'react';

interface ChatAndCommandFrameProps {
  username: string
}

const ChatAndCommandFrame: React.FC<ChatAndCommandFrameProps> = ({ username }) => {
  const chatEndpoint = 'ws://localhost:8080/game/chatEndpoint/';

  const [inputText, setInputText] = useState('');
  const [chatMessages, setChatMessages] = useState<string[]>([]);
  const [webSocket, setWebSocket] = useState<WebSocket | null>(null);


  useEffect(() => {
    console.log("username: " + username);
    
    const socket = new WebSocket(chatEndpoint + username);

    

    socket.onopen = () => {
      console.log('WebSocket connection opened');
      console.log('Endpoint' + chatEndpoint + username);
    };

    socket.onmessage = (event) => {
      const message = JSON.parse(event.data);
      console.log('Received message:', message);

      if (inputText.trim() !== '') {
        setChatMessages([...chatMessages, inputText]);
        setInputText('');
  
        sendMessage(inputText);
      }
    };

    socket.onclose = () => {
      console.log('WebSocket connection closed');
    };

    socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };

    setWebSocket(socket);

    // Clean up the WebSocket connection when the component unmounts
    return () => {
      if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        webSocket.close();
      }
    };
  }, [username]);

  const handleInputChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    setInputText(e.target.value);
  };

  const handleSendMessage = () => {
    if (inputText.trim() !== '') {
      setChatMessages([...chatMessages, inputText]);
      setInputText('');

      sendMessage(inputText);
    }
  };


  const sendMessage = (content: string) => {
    const message = {
      from: username,
      content,
    };

    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
      webSocket.send(JSON.stringify(message));
    } else {
      console.error('WebSocket connection not open.');
    }
  };

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
