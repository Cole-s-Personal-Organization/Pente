// WelcomePage.tsx

import React, { useState } from 'react';

interface WelcomePageProps {
    isConnectedToServer: boolean;
    setIsConnectedToServer: React.Dispatch<React.SetStateAction<boolean>>;
}

const WelcomePage: React.FC<WelcomePageProps> = ({isConnectedToServer, setIsConnectedToServer}) => {
  const [connecting, setConnecting] = useState(false);

  const handleConnectClick = () => {
    setConnecting(true);

    // Simulating socket connection (replace with your actual socket logic)
    setTimeout(() => {
      // Once connected, you can navigate to a different page or perform other actions
      // For now, just log a message
      console.log('Socket connected! Move to the next page...');
      setIsConnectedToServer(true);
    }, 1000);
  };

  return (
    <div >
      <div >Welcome to the Gameserver!</div>
      <button
        onClick={handleConnectClick}
        disabled={connecting}
      >
        {connecting ? 'Connecting...' : 'Connect'}
      </button>
    </div>
  );
};

const styles = {
  container: {
    height: '100vh',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
  },
  centeredText: {
    fontSize: '24px',
    marginBottom: '20px',
  },
  connectButton: {
    fontSize: '18px',
    padding: '10px',
    cursor: 'pointer',
    backgroundColor: '#4CAF50',
    color: 'white',
    border: 'none',
    borderRadius: '5px',
  },
};

export default WelcomePage;
