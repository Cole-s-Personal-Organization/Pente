import { useState, useEffect } from 'react'
import './App.css'
import MainPage from './pages/MainPage';
import WelcomePage from './pages/WelcomePage';
import ChatAndCommandFrame from './pages/components/chatAndCommandFrameElements/ChatAndCommandFrame';
import WebsocketEchoButton from './pages/components/WebsocketEchoButton';
import GameLobbies from './pages/GameLobbies';

function App() {
  console.log("App Rendered");

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
    <GameLobbies/>
  )
}

export default App
