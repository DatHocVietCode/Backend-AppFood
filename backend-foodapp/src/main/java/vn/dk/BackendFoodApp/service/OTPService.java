package vn.dk.BackendFoodApp.service;

import jakarta.mail.MessagingException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.dto.request.auth.OTPReceivedDTO;
import vn.dk.BackendFoodApp.model.OTP;
import vn.dk.BackendFoodApp.repository.OTPRepository;
import vn.dk.BackendFoodApp.utils.EmailUtils;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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
    public String sendOTP(String email) throws MessagingException, UnsupportedEncodingException {
        if (otpRepository.findByEmail(email).isEmpty())
        {
            String otpCode = createOTPCode();
            OTP otp = new OTP();
            otp.setEmail(email);
            otp.setValue(otpCode);
            otp.setCreatedAt(LocalDateTime.now());
            otp.setOtpToken(UUID.randomUUID().toString());
            otpRepository.save(otp);
        }
        Optional<OTP> sendingOTP = otpRepository.findByEmail(email);
        if (sendingOTP.isPresent())
        {
            emailUtils.sendOtpEmail(sendingOTP.get().getEmail(), sendingOTP.get().getValue());
            return sendingOTP.get().getOtpToken();
        }
        return null;
    }
    public boolean ValidateOTP(OTPReceivedDTO otpReceivedDTO)
    {
        Optional<OTP> optionalOTP = null;
        String email = otpReceivedDTO.getEmail();
        String otpToken = otpReceivedDTO.getOtpToken();
        String otpCode = otpReceivedDTO.getOtpCode();

        boolean isEmailExisted = false;
        boolean isTokenValid = false;
        boolean isOTPCodeValid = false;
        boolean isLiving = true;

        if (otpRepository.findByEmail(email).isPresent())
        {
            optionalOTP = otpRepository.findByEmail(email);
            isEmailExisted = true;
        }
        else
        {
            return false;
        }
        OTP otp = optionalOTP.get();
        LocalDateTime currentTime = LocalDateTime.now();
        if (Duration.between(otp.getCreatedAt(), currentTime).toMinutes() > 5)
        {
            isLiving = false;
        }
        if (otpCode.equals(otp.getValue()))
        {
            isOTPCodeValid = true;
        }
        if (otpToken.equals(otp.getOtpToken()))
        {
            isTokenValid = true;
        }
        return isTokenValid && isOTPCodeValid && isLiving;
    }
}
