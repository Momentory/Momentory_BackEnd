package com.example.momentory.domain.auth.controller;

import com.example.momentory.domain.auth.converter.AuthConverter;
import com.example.momentory.domain.auth.dto.AuthRequestDTO;
import com.example.momentory.domain.auth.dto.AuthResponseDTO;
import com.example.momentory.domain.auth.service.AuthService;
import com.example.momentory.domain.auth.service.MailService;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.ApiResponse;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final MailService mailService;
    private final UserService userService;

    @PostMapping("/userSignup")
    @Operation(summary = "사용자 회원가입 API", description = "일반 사용자 계정을 생성하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<AuthResponseDTO.SignResponseDTO> userSignup(@RequestBody AuthRequestDTO.SignRequestDTO signRequestDTO) {

        AuthConverter.UserRegistrationData user = AuthConverter.toUser(signRequestDTO);
        return ApiResponse.onSuccess(authService.signUp(user));
    }

    @PostMapping("/reissue")
    @Operation(summary = "Access 토큰 재발급 API", description = "만료된 access 토큰을 새로 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "access 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "access 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<Map<String, String>> reissue(@RequestBody AuthRequestDTO.RefreshRequestDTO request) {
        return ApiResponse.onSuccess(authService.reissueAccessToken(request.getRefreshToken()));
    }


    @PostMapping("/kakao/profile")
    @Operation(summary = "카카오 회원가입", description = "카카오 로그인의 사용자의 프로필을 설정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "access 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "access 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<AuthResponseDTO.SignResponseDTO> kakaoProfile( @RequestBody AuthRequestDTO.KakaoRequestDTO kakaoRequestDTO) {
        return ApiResponse.onSuccess(authService.setProfile(kakaoRequestDTO));
    }

    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복확인", description = "중복된 이메일이 있는지 확인합니다. ")
    public ApiResponse<String> checkEmail(@RequestParam String email) {
        authService.validateEmail(email);
        authService.duplicationCheckEmail(email);
        return ApiResponse.onSuccess("중복 없음");
    }

    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복확인", description = "중복된 닉네임이 있는지 확인합니다. ")
    public ApiResponse<String> checNickName(@RequestParam String nickName) {
        authService.duplicationCheckNickName(nickName);
        return ApiResponse.onSuccess("중복 없음");
    }

    @PostMapping("/send-email")
    @Operation(summary = "이메일 인증 링크 전송", description = "이메일 인증 링크를 전송합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<String> mailSend(@RequestBody String email) {
        if (email == null || email.isEmpty()) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_FOUND);
        }
        authService.duplicationCheckEmail(email);
        authService.validateEmail(email);
        mailService.sendMail(email);
        return ApiResponse.onSuccess("인증 링크가 이메일로 전송되었습니다.");
    }

    @GetMapping(value = "/verify-email", produces = "text/html; charset=UTF-8")
    @Operation(summary = "이메일 인증 확인", description = "이메일 인증 링크를 통해 인증을 확인합니다.")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean isVerified = mailService.verifyEmail(token);

        if (isVerified) {
            // 성공 시 HTML 응답
            String successHtml = """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <title>이메일 인증 완료</title>
                <style>
                    body {
                        font-family: 'Pretendard', sans-serif;
                        text-align: center;
                        background-color: #f9fafb;
                        margin-top: 120px;
                        color: #333;
                    }
                    .container {
                        display: inline-block;
                        padding: 40px 60px;
                        background-color: white;
                        border-radius: 16px;
                        box-shadow: 0 4px 16px rgba(0,0,0,0.1);
                    }
                    h1 { color: #16a34a; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>이메일 인증이 완료되었습니다!</h1>
                    <p>Momentory 서비스를 이용해주셔서 감사합니다.</p>
                    <p>이 창은 3초 뒤 자동으로 닫힙니다.</p>
                </div>
                <script>
                    setTimeout(() => window.close(), 3000);
                </script>
            </body>
            </html>
        """;
            return ResponseEntity.ok(successHtml);

        } else {
            // 실패 시 HTML 응답
            String failHtml = """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <title>이메일 인증 실패</title>
                <style>
                    body {
                        font-family: 'Pretendard', sans-serif;
                        text-align: center;
                        background-color: #f9fafb;
                        margin-top: 120px;
                        color: #333;
                    }
                    .container {
                        display: inline-block;
                        padding: 40px 60px;
                        background-color: white;
                        border-radius: 16px;
                        box-shadow: 0 4px 16px rgba(0,0,0,0.1);
                    }
                    h1 { color: #dc2626; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>이메일 인증에 실패했습니다.</h1>
                    <p>링크가 만료되었거나 잘못된 토큰입니다.</p>
                    <p>다시 이메일 인증을 요청해주세요.</p>
                </div>
            </body>
            </html>
        """;
            return ResponseEntity.badRequest().body(failHtml);
        }
    }


    @GetMapping("/check-email-verified")
    @Operation(summary = "이메일 인증 상태 확인", description = "이메일 인증 상태를 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<Boolean> checkEmailVerified(@RequestParam String email) {
        boolean isVerified = mailService.isEmailVerified(email);
        return ApiResponse.onSuccess(isVerified);
    }

    @GetMapping("/validate-password")
    @Operation(summary = "비밀번호 유효성 체크", description = "비밀번호 유효성을 체크합니다. ")
    public ApiResponse<String> validatePassword(@RequestParam String password) {
        authService.validatePassword(password);
        return ApiResponse.onSuccess("비밀번호가 유효합니다");
    }

    @PatchMapping("/change-password")
    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다. 사용자 이메일과 비밀번호를 주세요.")
    public ApiResponse<String> changePassword(@RequestParam String email, String password) {
        authService.changePassword(email,password);
        return ApiResponse.onSuccess("비밀번호가 변경되었습니다.");
    }

}
