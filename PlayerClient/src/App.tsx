import React, {useState, useEffect} from 'react';
import logo from './logo.svg';
import './App.css';
import MainPage from './pages/MainPage';
import WelcomePage from './pages/WelcomePage';

function App() {
  const [isConnectedToServer, setIsConnectedToServer] = useState<boolean>(false);
  const [sessionStartTime, setSessionStartTime] = useState<Date | null>(null);


  useEffect(() => {
    setSessionStartTime(new Date());
  }, [isConnectedToServer])

  return (
    <div className="App">
      {/* <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header> */}
      {sessionStartTime   
        ? <MainPage
            sessionStartTime={sessionStartTime}
          />
        : <WelcomePage
            isConnectedToServer={isConnectedToServer}
            setIsConnectedToServer={setIsConnectedToServer}
          />}
    </div>
  );
}

export default App;
