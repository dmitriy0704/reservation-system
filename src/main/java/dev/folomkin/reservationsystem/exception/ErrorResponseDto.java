package dev.folomkin.reservationsystem.exception;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String detailedMessage, // errorMessage
        LocalDateTime errorTime
) {
}
