package com.example.momentory.domain.auth.service;

import com.example.momentory.domain.auth.entity.EmailVerification;
import com.example.momentory.domain.auth.repository.EmailVerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    @Value("${APP_EMAIL}")
    private String senderEmail;

    @Value("${BASE_URL}")
    private String baseUrl;

    private final EmailVerificationRepository emailVerificationRepository;

    @Transactional
    public void sendMail(String mail) {
        // 기존 토큰 삭제
        emailVerificationRepository.deleteByEmail(mail);

        // 토큰 저장
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);
        EmailVerification emailVerification = EmailVerification.of(mail, token, expiryDate);
        emailVerificationRepository.save(emailVerification);

        // 실제 메일 전송은 비동기로
        sendMailAsync(mail, token);
    }

    @Async
    public void sendMailAsync(String mail, String token) {
        String verificationLink = baseUrl + "/api/auth/verify-email?token=" + token;

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("Momentory 이메일 인증");
            message.setText(createEmailBody(verificationLink), "UTF-8", "html");
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String createEmailBody(String verificationLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Momentory 이메일 인증</h1>
                    </div>
                    <div class="content">
                        <h2>안녕하세요!</h2>
                        <p>Momentory 회원가입을 위한 이메일 인증을 완료해주세요.</p>
                        <p>아래 버튼을 클릭하여 이메일 인증을 완료하세요:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">이메일 인증하기</a>
                        </p>
                        <p><strong>주의사항:</strong></p>
                        <ul>
                            <li>이 링크는 10분 후에 만료됩니다.</li>
                            <li>링크가 작동하지 않는 경우, 이메일을 다시 요청해주세요.</li>
                        </ul>
                    </div>
                    <div class="footer">
                        <p>이 이메일은 Momentory 회원가입 과정에서 발송되었습니다.</p>
                        <p>만약 회원가입을 요청하지 않으셨다면, 이 이메일을 무시하셔도 됩니다.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(verificationLink);
    }

    @Transactional
    public boolean verifyEmail(String token) {
        EmailVerification emailVerification = emailVerificationRepository.findByToken(token)
                .orElse(null);
        
        if (emailVerification == null) {
            return false; // 토큰이 존재하지 않음
        }
        
        if (emailVerification.isExpired()) {
            emailVerificationRepository.delete(emailVerification);
            return false; // 토큰이 만료됨
        }
        
        if (emailVerification.isVerified()) {
            return false; // 이미 인증됨
        }
        
        // 인증 완료 처리
        emailVerification.markAsVerified();
        emailVerificationRepository.save(emailVerification);
        return true;
    }

    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.findByEmail(email)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }
}
