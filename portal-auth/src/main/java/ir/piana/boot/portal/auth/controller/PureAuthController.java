package ir.piana.boot.portal.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kavenegar.sdk.excepctions.ApiException;
import com.kavenegar.sdk.excepctions.HttpException;
import ir.piana.boot.portal.auth.data.entity.MobileAsUserEntity;
import ir.piana.boot.portal.auth.data.service.UserManagementService;
import ir.piana.portal.common.errors.APIErrorType;
import ir.piana.portal.common.errors.APIResponseDto;
import ir.piana.portal.common.http.PortalRequestBody;
import ir.piana.portal.common.security.AuthenticatedInfo;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/auth")
public class PureAuthController {
    private final ObjectMapper objectMapper;
    private final UserManagementService userManagementService;

    public PureAuthController(
            ObjectMapper objectMapper,
            UserManagementService userManagementService) {
        this.objectMapper = objectMapper;
        this.userManagementService = userManagementService;
    }

//    @Secured("ROLE_ANONYMOUS")
//    @PreAuthorize()
    @PostMapping(value = "request-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseDto<String>> requestOtp(
            @RequestBody PortalRequestBody<SignInRequestDto> signInRequestDto,
            HttpSession session) {
        if (signInRequestDto == null) {
            return ResponseEntity.badRequest().build();
        }

        String captcha = (String) session.getAttribute("captcha");
        if (signInRequestDto.requestBody().captcha() == null ||
                !signInRequestDto.requestBody().captcha.equalsIgnoreCase(captcha)) {
            APIErrorType.SignInProvidedCaptchaIsIncorrect.doThrows();
        }

//        rateLimitService.checkSignInLimit(signInRequestDto.mobile);

        try {
            //ToDo should be generate random code
            String otp = "123456";
            /*if (!redisEntry.get(session.getId()).isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            redisEntry.put(session.getId(), new OtpSessionEntry(otp, signInRequestDto.mobile));*/

//            redisTemplate.restore(session.getId() + "signin.otp", otp.getBytes(), 120, TimeUnit.SECONDS, true);
//            KavenegarApi api = new KavenegarApi("6B6773663258696B304F65576F4433516739573856513D3D");

            // ToDo should be send otp
            /*SendResult Result = api.verifyLookup(
                    userMobile.mobile,
                    otp,
                    "lineup-verify");*/
        } catch (HttpException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("HttpException  : " + ex.getMessage());
        } catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("ApiException : " + ex.getMessage());
        }

        return ResponseEntity.ok(new APIResponseDto(true, "sended!"));
    }

    @PostMapping(value = "verify-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VerifyOTPResponseDto> verifyOtp(
            @RequestBody PortalRequestBody<VerifyOTPRequestDto> verifyOTPRequestDto,
            HttpSession session) {
        if (verifyOTPRequestDto == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            /*OtpSessionEntry otpSessionEntry = redisEntry.getAndDelete(session.getId())
                    .orElseThrow(APIErrorType.SignInHaveNotAnyActiveOTP::getException);*/

            /*if (!otpSessionEntry.getOtp().equalsIgnoreCase(verifyOTPRequestDto.otp)) {
                APIErrorType.SignInProvidedOTPIsIncorrect.doThrows();
            }*/

            MobileAsUserEntity mobileAsUserEntity = userManagementService.findOrRegister(/*otpSessionEntry.getForMobile()*/"");
            AuthenticatedInfo authenticatedUser = new AuthenticatedInfo(
                    mobileAsUserEntity.getMobile(),
                    mobileAsUserEntity.getMobile(),
                    null,
                    List.of("ROLE_USER"));

            return ResponseEntity.ok(new VerifyOTPResponseDto(mobileAsUserEntity.getMobile()));
        } catch (HttpException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطا رخ می دهد.
            System.out.print("HttpException  : " + ex.getMessage());
        } catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("ApiException : " + ex.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    public record SignInRequestDto(String mobile, String captcha) {
    }

    public record VerifyOTPRequestDto(String otp) {
    }

    public record VerifyOTPResponseDto(String mobile) {
    }
}
