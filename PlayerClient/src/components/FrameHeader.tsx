

import React from 'react'

interface FrameHeaderProps {
    frameName: string
}

const FrameHeader: React.FC<FrameHeaderProps> = ({ frameName }) => {
  return (
    <div>
        <p>Frame</p>
        <p>{ frameName }</p>
    </div>
  )
}

export default FrameHeader;