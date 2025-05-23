import React from 'react';

const Widget = ({ title, value, percentage, isIncrease, description }) => {
  return (
    <div className="widget-container">
      <div className="widget-header">
        <h3>{title}</h3>
      </div>
      <div className="widget-value">
        <span>{value}</span>
      </div>
      <div className="widget-footer">
        <div className="widget-percentage">
          <span
            className={`material-symbols-outlined ${
              isIncrease ? 'increase-icon' : 'decrease-icon'
            }`}
          >
            {isIncrease ? 'arrow_upward' : 'arrow_downward'}
          </span>
          <span className="percentage">{percentage}%</span>
        </div>
        <div className="widget-description">{description}</div>
      </div>
    </div>
  );
};

export default Widget;

