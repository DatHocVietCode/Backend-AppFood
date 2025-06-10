package vn.dk.BackendFoodApp.service;

import jakarta.mail.MessagingException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.model.OTP;
import vn.dk.BackendFoodApp.repository.OTPRepository;
import vn.dk.BackendFoodApp.utils.EmailUtils;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;

@Service
@NoArgsConstructor
public class OTPService {
    @Autowired
    OTPRepository otpRepository;
    @Autowired
    EmailUtils emailUtils;
    public String createOTPCode ()
    {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    public void sendOTP(String email) throws MessagingException, UnsupportedEncodingException {
        if (otpRepository.findByEmail(email).isEmpty())
        {
            String otpCode = createOTPCode();
            OTP otp = new OTP();
            otp.setEmail(email);
            otp.setValue(otpCode);
            otpRepository.save(otp);
        }
        Optional<OTP> sendingOTP = otpRepository.findByEmail(email);
        if (sendingOTP.isPresent())
        {
            emailUtils.sendOtpEmail(sendingOTP.get().getEmail(), sendingOTP.get().getValue());
        }
    }
}
