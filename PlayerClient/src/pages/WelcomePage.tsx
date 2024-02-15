// WelcomePage.tsx

import { Button } from '@/components/ui/button';
import React, { useState } from 'react';

interface WelcomePageProps {
  
}

const WelcomePage: React.FC<WelcomePageProps> = () => {
  const [connecting, setConnecting] = useState(false);

  const handleConnectClick = () => {
    setConnecting(true);
    
    setTimeout(() => {
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
