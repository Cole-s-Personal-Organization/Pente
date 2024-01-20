// WelcomePage.tsx

import { Button } from '@/components/ui/button';
import React, { useState } from 'react';

interface WelcomePageProps {
    setIsConnectedToServer: React.Dispatch<React.SetStateAction<boolean>>;
}

const WelcomePage: React.FC<WelcomePageProps> = ({setIsConnectedToServer}) => {
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
    <div id='welcome-page'>
      <h1 className='text-3xl font-bold'>Game Server</h1>
      <Button onClick={handleConnectClick} disabled={connecting}>
        {connecting ? 'Connecting...' : 'Connect'}
      </Button>
    </div>
  );
};

export default WelcomePage;
