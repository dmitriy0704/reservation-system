package dev.folomkin.reservationsystem.reservations.availability;

public record CheckAvailabilityResponse(
        String message,

        AvailabilityStatus status
) {
}
