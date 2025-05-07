const htmlEmailVerify = async (generateOTP, duration = 10) => {
  return `
    <!DOCTYPE html>
    <html lang="vi">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Xác Thực Email</title>
      <style>
        body {
          font-family: Arial, sans-serif;
          background-color: #f4f4f4;
          margin: 0;
          padding: 0;
        }
        .container {
          max-width: 600px;
          margin: 20px auto;
          background-color: #ffffff;
          padding: 20px;
          border-radius: 8px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .header {
          text-align: center;
          padding: 20px 0;
        }
        .header h1 {
          color: #333333;
          margin: 0;
          font-size: 24px;
        }
        .content {
          padding: 20px;
          text-align: center;
        }
        .otp-code {
          font-size: 32px;
          font-weight: bold;
          color: #2c3e50;
          background-color: #ecf0f1;
          padding: 10px 20px;
          display: inline-block;
          border-radius: 5px;
          margin: 20px 0;
        }
        .content p {
          color: #666666;
          line-height: 1.6;
          margin: 10px 0;
        }
        .footer {
          text-align: center;
          padding: 20px;
          font-size: 14px;
          color: #999999;
        }
        .footer a {
          color: #3498db;
          text-decoration: none;
        }
      </style>
    </head>
    <body>
      <div class="container">
        <div class="header">
          <h1>Xác Thực Email</h1>
        </div>
        <div class="content">
          <p>Xin chào,</p>
          <p>Chúng tôi đã nhận được yêu cầu xác thực địa chỉ email của bạn. Vui lòng sử dụng mã OTP bên dưới để hoàn tất quá trình xác thực:</p>
          <div class="otp-code">${generateOTP}</div>
          <p>Mã này có hiệu lực trong <strong>${duration} phút</strong>. Nếu bạn không yêu cầu xác thực này, vui lòng bỏ qua email này hoặc liên hệ với đội ngũ hỗ trợ của chúng tôi.</p>
        </div>
        <div class="footer">
          <p>Cần hỗ trợ? <a href="mailto:ho tro@example.com">Liên hệ đội ngũ hỗ trợ</a></p>
          <p>© 2025 Công ty của bạn. Đã đăng ký bản quyền.</p>
        </div>
      </div>
    </body>
    </html>
            `;
};

export default htmlEmailVerify;
