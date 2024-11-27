package com.haru.doyak.harudoyak.domain.auth;

import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String mailUsername;
    @Value("${spring.mail.verify-uri-local}")
    private String mailVerifyUriLocal;
    @Value("${spring.mail.verify-uri-site}")
    private String mailVerifyUriSite;

    public void sendAuthLinkEmail(String referrer, String recipient) {
        try {
            String mailVerifyUri = getMailVerifyUri(referrer);
            String authLink = mailVerifyUri + "email=" + recipient + "&isVerified=true&expireDate=" + LocalDateTime.now().plusDays(1);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient);
            helper.setSubject("[하루도약] 이메일 인증 요청");
            String htmlContent = "<h1>하루도약에 오신 걸 환영합니다.</h1>" +
                    "<p>아래 링크를 클릭하여 회원가입을 완료하세요</p>" +
                    "<a href=" + authLink + ">회원가입 링크</a>" +
                    "<p>감사합니다!</p>";
            helper.setText(htmlContent, true);
            helper.setFrom(mailUsername);
            javaMailSender.send(message);
        }
        catch (MessagingException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }

    public String getMailVerifyUri(String referrer){
        if(referrer!= null &&
                referrer.startsWith("https://www.harudoyak.site") ||
                referrer.startsWith("https://harudoyak.site")) return mailVerifyUriSite;
        else return mailVerifyUriLocal;
    }

    public void sendTempPasswordEmail(String recipient, String password) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient);
            helper.setSubject("[하루도약] 임시 비밀번호 발급");
            String htmlContent = "<h1>하루도약에 오신 걸 환영합니다.</h1>" +
                    "<p>아래 임시 비밀번호로 로그인 후 변경해주세요</p>" +
                    "<p>" + password + "</p>" +
                    "<p>감사합니다!</p>";
            helper.setText(htmlContent, true);
            helper.setFrom(mailUsername);
            javaMailSender.send(message);
        }
        catch (MessagingException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }
}
