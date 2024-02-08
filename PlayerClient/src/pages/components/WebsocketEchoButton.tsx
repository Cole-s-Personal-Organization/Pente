

import { Button } from '@/components/ui/button'
import React, { useState, useEffect } from 'react'

const WebsocketPingButton: React.FC = () => {
    const echoEndpoint = 'ws://localhost:8080/game/websocket/echo';


    const webSocket = new WebSocket(echoEndpoint);

    webSocket.onopen = (event: Event) => {
        console.log("WebSocket connection opened");
        sendMessage("Hello, Jakarta WebSocket!");
    };

    webSocket.onmessage = (event: MessageEvent) => {
        const receivedMessage = event.data;
        console.log("Received message:", receivedMessage);
    };

    webSocket.onclose = (event: CloseEvent) => {
        console.log("WebSocket connection closed", event);
    };

    webSocket.onerror = (event: Event) => {
        console.error("WebSocket error:", event);
    };

    function sendMessage(message: string) {
        if (webSocket.readyState === WebSocket.OPEN) {
            console.log("Sending message:", message);
            webSocket.send(message);
        } else {
            console.error("WebSocket connection not open.");
        }
    }

    const handleButtonClick = () => {

    }

    return (
        <div>
            {/* Send Message Button */}
            <Button onClick={handleButtonClick} style={{ marginTop: '10px' }}>
                Send Message
            </Button>
        </div>
    );
}

export default WebsocketPingButton