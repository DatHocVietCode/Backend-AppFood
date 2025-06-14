package vn.dk.BackendFoodApp.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}") // Inject the email address configured in application.yml
    private String fromEmail;
    /**
     * Gửi email chứa mã OTP (One-Time Password) đến địa chỉ email của người dùng.
     *
     * @param toEmail Địa chỉ email của người nhận.
     * @param otp Mã OTP cần gửi.
     * @throws MessagingException Nếu có lỗi xảy ra trong quá trình gửi email.
     * @throws UnsupportedEncodingException Nếu có lỗi liên quan đến encoding.
     */
    public void sendOtpEmail(String toEmail, String otp) throws MessagingException, UnsupportedEncodingException {
        // Tạo một đối tượng MimeMessage mới
        MimeMessage message = mailSender.createMimeMessage();
        // MimeMessageHelper giúp dễ dàng thiết lập các thuộc tính của email (người gửi, người nhận, chủ đề, nội dung, hỗ trợ HTML)
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Thiết lập thông tin người gửi (có thể là tên hiển thị thân thiện)
        helper.setFrom(fromEmail, "FoodApp Support"); // Thay 'FoodApp Support' bằng tên mong muốn

        // Thiết lập địa chỉ người nhận
        helper.setTo(toEmail);

        // Thiết lập chủ đề của email
        helper.setSubject("Mã xác minh OTP cho tài khoản FoodApp của bạn");

        // Thiết lập nội dung email (có thể là HTML)
        String emailContent = "<p>Xin chào,</p>"
                + "<p>Mã OTP của bạn để xác minh tài khoản FoodApp là:</p>"
                + "<h2 style='color: #007bff;'>" + otp + "</h2>"
                + "<p>Mã này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>"
                + "<p>Trân trọng,<br>Đội ngũ FoodApp</p>";
        helper.setText(emailContent, true); // true để cho phép nội dung HTML

        // Gửi email
        mailSender.send(message);

        System.out.println("Email OTP đã được gửi tới: " + toEmail);
    }
}
