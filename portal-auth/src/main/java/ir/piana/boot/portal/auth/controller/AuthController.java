package ir.piana.boot.portal.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("api/v1/auth")
public class AuthController {
    //    private final RedisTemplate<String, String> redisTemplate;
 /*   private final RedisEntry<OtpSessionEntry> redisEntry;
    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;
    private final UserManagementService userManagementService;

    public AuthController(
            @Qualifier("redisOtpSessionEntry") RedisEntry<OtpSessionEntry> redisEntry,
            RateLimitService rateLimitService,
            ObjectMapper objectMapper,
            UserManagementService userManagementService) {
        this.redisEntry = redisEntry;
        this.rateLimitService = rateLimitService;
        this.objectMapper = objectMapper;
        this.userManagementService = userManagementService;
    }

//    @Secured("ROLE_ANONYMOUS")
//    @PreAuthorize()
    @PostMapping(value = "request-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseDto<String>> requestOtp(
            @RequestBody SignInRequestDto signInRequestDto,
            HttpSession session,
            SecurityContext securityContext) {
        if (signInRequestDto == null) {
            return ResponseEntity.badRequest().build();
        }

        *//*String captcha = (String) session.getAttribute("captcha-for-signin");
        if (signInRequestDto.captcha == null || !signInRequestDto.captcha.equalsIgnoreCase(captcha)) {
            APIErrorType.SignInProvidedCaptchaIsIncorrect.doThrows();
        }*//*

        rateLimitService.checkSignInLimit(signInRequestDto.mobile);

        try {
            //ToDo should be generate random code
            String otp = "123456";
            if (!redisEntry.get(session.getId()).isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            redisEntry.put(session.getId(), new OtpSessionEntry(otp, signInRequestDto.mobile));

//            redisTemplate.restore(session.getId() + "signin.otp", otp.getBytes(), 120, TimeUnit.SECONDS, true);
//            KavenegarApi api = new KavenegarApi("6B6773663258696B304F65576F4433516739573856513D3D");

            // ToDo should be send otp
            *//*SendResult Result = api.verifyLookup(
                    userMobile.mobile,
                    otp,
                    "lineup-verify");*//*
        } catch (HttpException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("HttpException  : " + ex.getMessage());
        } catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("ApiException : " + ex.getMessage());
        }

        return ResponseEntity.ok(new APIResponseDto(true, "sended!"));
    }

    @PostMapping(value = "verify-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VerifyOTPResponseDto> verifyOtp(
            @RequestBody VerifyOTPRequestDto verifyOTPRequestDto,
            HttpSession session,
            SecurityContext securityContext) {
        SecurityContext sc = SecurityContextHolder.getContext();

        if (verifyOTPRequestDto == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            OtpSessionEntry otpSessionEntry = redisEntry.getAndDelete(session.getId())
                    .orElseThrow(APIErrorType.SignInHaveNotAnyActiveOTP::getException);

            if (!otpSessionEntry.getOtp().equalsIgnoreCase(verifyOTPRequestDto.otp)) {
                APIErrorType.SignInProvidedOTPIsIncorrect.doThrows();
            }

            MobileAsUserEntity mobileAsUserEntity = userManagementService.findOrRegister(otpSessionEntry.getForMobile());
            AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                    List.of(new PianaGrantedAuthority("ROLE_USER")),
                    mobileAsUserEntity);
            securityContext.setAuthentication(authenticatedUser);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
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
    }*/
}
