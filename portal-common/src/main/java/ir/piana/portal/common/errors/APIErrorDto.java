package ir.piana.portal.common.errors;

import org.springframework.http.HttpStatus;

public record APIErrorDto(HttpStatus httpStatus, String errorMessage) {
    APIResponseDto<String> toResponseDto() {
        return new APIResponseDto<>(false, errorMessage);
    }
}
