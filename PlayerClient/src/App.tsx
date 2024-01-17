import { useState, useEffect } from 'react'
import './App.css'
import MainPage from './pages/MainPage';
import WelcomePage from './pages/WelcomePage';

function App() {
  const [isConnectedToServer, setIsConnectedToServer] = useState<boolean>(false);
  const [sessionStartTime, setSessionStartTime] = useState<Date | null>(null);

  const socketConnection = new WebSocket('ws://localhost:8080');


  useEffect(() => {
    setSessionStartTime(new Date());
  }, [isConnectedToServer])

  return (
    <>
      {(isConnectedToServer && sessionStartTime)   
        ? <MainPage
            sessionStartTime={sessionStartTime}
          />
        : <WelcomePage
            isConnectedToServer={isConnectedToServer}
            setIsConnectedToServer={setIsConnectedToServer}
          />}
    </>
  )
}

export default App
