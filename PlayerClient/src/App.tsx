import { useState, useEffect } from 'react'
import './App.css'
import MainPage from './pages/MainPage';
import WelcomePage from './pages/WelcomePage';
import ChatAndCommandFrame from './pages/components/chatAndCommandFrameElements/ChatAndCommandFrame';
import WebsocketEchoButton from './pages/components/WebsocketEchoButton';

function App() {
  console.log("App Rendered");
  
  const [isConnectedToServer, setIsConnectedToServer] = useState<boolean>(false);
  const [sessionStartTime, setSessionStartTime] = useState<Date | null>(null);


  useEffect(() => {
    console.log("App useEffect Triggered");
    
    setSessionStartTime(new Date());
  }, [isConnectedToServer])

  // return (
  //   <>
  //     {(isConnectedToServer && sessionStartTime)   
  //       ? <MainPage
  //           sessionStartTime={sessionStartTime}
  //         />
  //       : <WelcomePage
  //           setIsConnectedToServer={setIsConnectedToServer}
  //         />}
  //   </>
  // )

  return (
    // <ChatAndCommandFrame username='cole'/>
    <WebsocketEchoButton/>
  )
}

export default App
