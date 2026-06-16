package az.msorder.dto.errorDto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String path
) {
}
