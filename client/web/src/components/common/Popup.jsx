import React, { useState } from 'react';
import '../../styles/popup.css';

const COLOR_MAP = {
  success: '#3fbf48',
  warning: '#ff9800',
  error: '#d32f2f',
  info: '#1976d2',
  gray: '#888',
  white: '#fff',
};

const Popup = ({
  open,
  title,
  titleColor = 'info', // success | warning | error | info
  content,
  contentColor = 'info',
  detail,
  detailColor = 'info',
  inputs = [],
  showImageUpload = false,
  imageUrl,
  onImageChange,
  onInputChange,
  onClose,
  onConfirm,
  cancelText = 'Hủy',
  confirmText = 'Đồng ý',
  cancelColor = 'gray',
  confirmColor = 'info',
  loading = false,
  showCancel = true,
  showConfirm = true,
  children,
}) => {
  const [confirmHover, setConfirmHover] = useState(false);
  const [cancelHover, setCancelHover] = useState(false);

  if (!open) return null;

  const confirmMain = COLOR_MAP[confirmColor] || confirmColor;
  const cancelMain = COLOR_MAP[cancelColor] || cancelColor;

  return (
    <div className="modal-backdrop">
      <div className="modal-confirm">
        {title && (
          <h3 style={{ color: COLOR_MAP[titleColor] || titleColor }}>{title}</h3>
        )}
        {content && (
          <p style={{ color: COLOR_MAP[contentColor] || contentColor }}>{content}</p>
        )}
        {detail && (
          <div style={{ margin: '12px 0', color: COLOR_MAP[detailColor] || detailColor, fontWeight: 500 }}>
            {detail}
          </div>
        )}

        {inputs.map((input, idx) => (
            <div key={idx} style={{ width: '100%', marginBottom: 12, textAlign: 'left' }}>
                {input.label && (
                <label
                    htmlFor={input.name}
                    style={{
                    display: 'block',
                    marginBottom: 4,
                    fontWeight: 500,
                    fontFamily: 'Quicksand, sans-serif',
                    fontSize: 15,
                    color: '#222'
                    }}
                >
                    {input.label}
                </label>
            )}
            {input.type === 'textarea' ? (
            <textarea
                id={input.name}
                placeholder={input.placeholder}
                value={input.value}
                onChange={e => onInputChange && onInputChange(input.name, e.target.value)}
                style={input.style || { width: '100%', minHeight: 60, padding: 8, borderRadius: 8, border: '1px solid #ccc' }}
            />
            ) : (
            <input
                id={input.name}
                type={input.type || 'text'}
                placeholder={input.placeholder}
                value={input.value}
                onChange={e => onInputChange && onInputChange(input.name, e.target.value)}
                style={input.style || { width: '100%', padding: 8, borderRadius: 8, border: '1px solid #ccc' }}
            />
            )}
        </div>
        ))}

        {/* Upload ảnh */}
        {showImageUpload && (
          <>
            <input
              type="file"
              accept="image/*"
              onChange={e => {
                if (onImageChange) onImageChange(e);
              }}
              style={{ marginBottom: 12, fontFamily: 'Quicksand, sans-serif', fontSize: 14, color: '#888' }}
            />
            {imageUrl && (
              <img
                src={imageUrl}
                alt="preview"
                style={{ width: 80, height: 80, objectFit: 'cover', borderRadius: 8, border: '1px solid #eee', marginBottom: 8 }}
              />
            )}
          </>
        )}

        {/* Nội dung custom nếu muốn */}
        {children}

        <div className="modal-actions">
          {showCancel && (
            <button
              className="btn-cancel"
              style={{
                background: '#fff',
                color: cancelHover ? '#3fbf48' : '#888',
                borderColor: cancelHover ? '#3fbf48' : '#888',
              }}
              onMouseEnter={() => setCancelHover(true)}
              onMouseLeave={() => setCancelHover(false)}
              onClick={onClose}
              disabled={loading}
            >
              {cancelText}
            </button>
          )}
          {showConfirm && (
            <button
              className="btn-delete"
              style={{
                background: confirmHover ? '#fff' : confirmMain,
                color: confirmHover ? confirmMain : '#fff',
                borderColor: confirmMain,
              }}
              onMouseEnter={() => setConfirmHover(true)}
              onMouseLeave={() => setConfirmHover(false)}
              onClick={onConfirm}
              disabled={loading}
            >
              {loading ? 'Đang xử lý...' : confirmText}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default Popup;