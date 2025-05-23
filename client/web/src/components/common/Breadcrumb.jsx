import '../../styles/breadcrumb.css';

export default function Breadcrumb({ title, paths }) {
    return (
      <div className="breadcrumb-container">
        <h2 className="page-title">{title}</h2>
        <p className="page-subtitle">
          {paths.join(' > ')}
        </p>
      </div>
    );
  }
