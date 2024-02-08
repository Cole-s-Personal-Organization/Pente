import React, { useState, useEffect } from 'react';

interface TimeDisplayComponentProps {
    connectionStartTime: Date
}

function calculateTimeDelta(startTime: Date, endTime: Date): number {
  const difference = endTime.getTime() - startTime.getTime();
  const seconds = Math.floor(difference / 1000);
  return seconds;
}

const TimeDisplayComponent: React.FC<TimeDisplayComponentProps> = ({ connectionStartTime }) => {
  const [currentTime, setCurrentTime] = useState(new Date());
  const [timeDeltaSinceStartSeconds, setCurrentTimeDeltaSinceStartSeconds] = useState<number>(
    calculateTimeDelta(connectionStartTime, currentTime)
  );

  // Simulating socket connection (replace with your actual socket logic)
  useEffect(() => {

    // Update time every second
    const interval = setInterval(() => {
      const currentTime = new Date();
      setCurrentTime(currentTime);
      
      const timeDelta = calculateTimeDelta(connectionStartTime, currentTime);
      setCurrentTimeDeltaSinceStartSeconds(timeDelta);
    }, 60000);

    return () => clearInterval(interval);
  }, []);

  const formatTime = (time: Date) => {
    return time.toLocaleTimeString();
  };


  return (
    <div>
      <h2>Time Display</h2>
      <p>Date: { currentTime.getMonth() + 1 }/{ currentTime.getDate() }/{ currentTime.getFullYear() }</p>
      <p>Current Time: {formatTime(currentTime)}</p>
      <p>Time Since Connection Delta: { Math.floor(timeDeltaSinceStartSeconds / 60) } minutes</p>
    </div>
  );
};

export default TimeDisplayComponent;
