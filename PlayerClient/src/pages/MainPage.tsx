

import React from 'react'
// import NetworkMetricsComponent from './components/systemFrameElements/NetworkMetricsComponent'
// import TimeDisplayComponent from './components/systemFrameElements/TimeDisplayComponent'
import SystemFrame from './components/systemFrameElements/SystemFrame'
import ChatAndCommandFrame from './components/chatAndCommandFrameElements/ChatAndCommandFrame'
// import { ResizablePanelGroup } from '@/components/ui/resizable'

interface MainPageProps {
  sessionStartTime: Date
}

const MainPage: React.FC<MainPageProps> = ({ sessionStartTime }) => {

  return (
    <div className='page'>
      
      <SystemFrame sessionStartTime={sessionStartTime}/>
      <ChatAndCommandFrame username='test'/>
    </div>
  )
}

export default MainPage