

import React from 'react'

import './FrameHeader.css'

interface FrameHeaderProps {
    frameName: string
}

const FrameHeader: React.FC<FrameHeaderProps> = ({ frameName }) => {
  return (
    <div className='frame-header'>
        <p>Frame</p>
        <p className='frame-header-name'>{ frameName }</p>
    </div>
  )
}

export default FrameHeader;