function tokenEmailContent(token){
    return `<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;">
    <div
        style="max-width: 600px; margin: 20px auto; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); background-color: #fff;">
        <div style="background-color: #435ebe; color: white; padding: 10px 20px; border-radius: 5px; font-size: 20px;">
            Account Registration Confirmation
        </div>
        <div style="padding: 20px; font-size: 16px; color: #333;">
            <p>Hello,</p>

            <p>We have received a request to register an account from you. To complete the registration process, please click
                the button below to confirm your account:</p>

            <p style="text-align: center;">
                <a href="http://localhost:8080/register_confirmation?token=${token}"
                    style="background-color: #435ebe; color: white; padding: 10px 20px; text-align: center; text-decoration: none; font-size: 16px; border-radius: 5px; display: inline-block;">Confirm
                    Account Registration</a>
            </p>

            <p>This confirmation link will expire after 24 hours. If you did not request registration, please ignore this email.</p>
        </div>
        <div style="font-size: 14px; text-align: center; color: #777; padding-top: 20px;">
            <p>Best regards,</p>
            <p><strong>Human Resources Department</strong><br />
                ABC Ltd.<br />
                Phone: 0123 456 789 | Email: hr@company.com</p>
        </div>
    </div>
</body>`;
}
