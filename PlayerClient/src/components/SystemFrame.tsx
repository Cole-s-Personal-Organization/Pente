

import React from 'react'
import FrameHeader from './FrameHeader'
import TimeDisplayComponent from './TimeDisplayComponent'
import NetworkMetricsComponent from './NetworkMetricsComponent'

interface SystemFrameProps {
  sessionStartTime: Date
}

const SystemFrame: React.FC<SystemFrameProps> = ({ sessionStartTime }) => {
  return (
    <div>
      <FrameHeader frameName='System'/>

      <TimeDisplayComponent
        connectionStartTime={sessionStartTime}
      />
      <NetworkMetricsComponent/>
    </div>
  )
}

export default SystemFrame;