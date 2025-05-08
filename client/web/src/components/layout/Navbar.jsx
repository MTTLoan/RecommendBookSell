import '../../styles/navbar.css';
import defaultAvatar from '../../assets/images/default_avatar.png';
import { useNavigate } from 'react-router-dom';

export default function Navbar({ user }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const avatarSrc = user?.avatar ? user.avatar : defaultAvatar;

  return (
    <div className="navbar">
      <div className="navbar-left">
        <span className="material-symbols-outlined menu-toggle">arrow_drop_down</span>
      </div>
      <div className="navbar-right">
        <img src={avatarSrc} alt="User Avatar" className="avatar" />
        <div className="user-info">
          <p className="full-name">{user.fullName}</p>
          <p className="username">{user.username}</p>
        </div>
        <button className="logout-btn" onClick={handleLogout} title="Đăng xuất">
          <span className="material-symbols-outlined">logout</span>
        </button>
      </div>
    </div>
  );
}
