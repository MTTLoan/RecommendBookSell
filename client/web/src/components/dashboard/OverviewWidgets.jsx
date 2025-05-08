import React from 'react';
import '../../styles/overviewWidgets.css';

export default function OverviewWidgets({ stats }) {
    return (
      <div className="widgets">
        {stats?.map((item, index) => (
          <div className="widget" key={index}>
            <h4>{item.label}</h4>
            <h2>{item.value}</h2>
            <span className={item.change >= 0 ? 'positive' : 'negative'}>
            <span className="material-symbols-outlined">
                {item.change >= 0 ? 'trending_up' : 'trending_down'}
              </span>
              {Math.abs(item.change)}%
            </span>
          </div>
        ))}
      </div>
    );
  }
  