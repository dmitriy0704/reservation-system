package dev.folomkin.reservationsystem.service;

import dev.folomkin.reservationsystem.entity.ReservationEntity;
import dev.folomkin.reservationsystem.model.Reservation;
import dev.folomkin.reservationsystem.model.ReservationStatus;
import dev.folomkin.reservationsystem.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReservationService {

    public final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation getReservationById(Long id) {
        ReservationEntity reservationEntity = repository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        return toDomainReservation(reservationEntity);
    }


    public List<Reservation> findAllReservations() {
        List<ReservationEntity> allEntities = repository.findAll();
        return allEntities.stream().map(this::toDomainReservation).toList();
    }


    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        if (!reservationToCreate.endDate().isAfter(reservationToCreate.startDate())) {
            throw new IllegalArgumentException("Start date must be 1 day earlier than and date");
        }
        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );
        var saved = repository.save(entityToSave);
        return toDomainReservation(saved);
    }

    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {
        var reservationEntity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found reservation by id = " + id)
        );

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot modify reservation " + reservationEntity.getStatus());
        }
        if (!reservationToUpdate.endDate().isAfter(reservationToUpdate.startDate())) {
            throw new IllegalArgumentException("Start date must be 1 day earlier than and date");
        }
        var reservationToSave = new ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        var updatedReservation = repository.save(reservationToSave);
        return toDomainReservation(updatedReservation);
    }


    @Transactional
    public void cancelReservation(Long id) {
        var reservation = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id " + id));

        if (reservation.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new IllegalStateException("Cannot cancel approved reservation. Contact with manager please.");
        }
        if (reservation.getStatus().equals(ReservationStatus.CANCELLED)) {
            throw new IllegalStateException("Cannot cancel the reservation. Reservation was already canceled.");
        }
        repository.setStatus(id, ReservationStatus.CANCELLED);
        log.info("Successfully cancelled reservation: id={}", id);
    }


    public Reservation approveReservation(Long id) {
        var reservationEntity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found reservation by id = " + id)
        );
        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot approve reservation status" + reservationEntity.getStatus());
        }
        var isConflict = isReservationConflict(reservationEntity);
        if (isConflict) {
            throw new IllegalStateException("Cannot approve reservation because of conflict");
        }


        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);
        return toDomainReservation(reservationEntity);
    }


    private boolean isReservationConflict(ReservationEntity reservationEntity) {
        var allReservations = repository.findAll();

        for (ReservationEntity existingReservation : allReservations) {
            if (reservationEntity.getId().equals(existingReservation.getId())) {
                continue;
            }
            if (!reservationEntity.getRoomId().equals(existingReservation.getRoomId())) {
                continue;
            }
            if (!existingReservation.getStatus().equals(ReservationStatus.APPROVED)) {
                continue;
            }
            if (reservationEntity.getStartDate().isBefore(existingReservation.getEndDate())
                    && existingReservation.getStartDate().isBefore(reservationEntity.getEndDate())) {
                return true;
            }
        }
        return false;
    }


    private Reservation toDomainReservation(ReservationEntity reservationEntity) {
        return new Reservation(
                reservationEntity.getId(),
                reservationEntity.getUserId(),
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate(),
                reservationEntity.getStatus()
        );
    }
}
