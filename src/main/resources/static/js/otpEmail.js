function otpEmail(otp) {
    return `<body style="font-family: Arial, sans-serif;">
    <div
    style="width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);">
        <div
    style="background-color: #435ebe; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 8px; border-top-right-radius: 8px;">
        <h2>OTP Verification</h2>
</div>
    <div style="padding: 20px; color: #333333;">
        <p>Dear User,</p>
        <p>Thank you for using our service. To complete your verification, please use the OTP code below:</p>
        <p style="font-size: 24px; font-weight: bold; color: #435ebe; margin: 20px 0;">${otp}</p>
        <p>This code will expire in 3 minutes. If you did not request this, please ignore this email.</p>
        <a href="#"
           style="display: inline-block; padding: 10px 20px; background-color: #435ebe; color: #ffffff; text-decoration: none; border-radius: 5px; text-align: center; margin-top: 20px;">Verify
            Now</a>
    </div>
    <div style="padding: 10px; background-color: #f4f7fa; text-align: center; font-size: 12px; color: #999999; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;">
        <p>If you have any questions, feel free to contact our support team.</p>
    </div>
</div>
</body>`;
}