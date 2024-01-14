

import React from 'react'
import NetworkMetricsComponent from '../components/NetworkMetricsComponent'
import TimeDisplayComponent from '../components/TimeDisplayComponent'
import SystemFrame from '../components/SystemFrame'

interface MainPageProps {
  sessionStartTime: Date
}

const MainPage: React.FC<MainPageProps> = ({ sessionStartTime }) => {

  return (
    <div>MainPage
      
      <SystemFrame sessionStartTime={sessionStartTime}/>
    </div>
  )
}

export default MainPage