package dev.folomkin.reservationsystem.reservations;

import org.springframework.web.bind.annotation.RequestParam;

public record ReservationSearchFilter(
        Long roomId,
        Long userId,
        Integer pageSize,
        Integer pageNumber
) {
}
