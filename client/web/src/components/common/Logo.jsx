// src/components/common/Logo.jsx
import React from 'react';
import logoImage from '../../assets/images/logo_1.png';

const Logo = ({ className = '', size = 'default' }) => {
  const sizeClasses = {
    small: 'h-6',
    default: 'h-10',
    large: 'h-16',
  };

  return (
    <div className={`flex items-center ${className}`}>
      <img 
        src={logoImage} 
        alt="Logo" 
        className={`${sizeClasses[size] || sizeClasses.default}`} 
      />
    </div>
  );
};

export default Logo;