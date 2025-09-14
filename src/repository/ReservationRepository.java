package repository;

import java.util.List;
import java.util.Optional;

import modal.Reservation;

public interface ReservationRepository {

    void save(Reservation reservation);
    void update(Reservation reservation);
    List<Reservation> findActiveQueue(String isbn, String branchId);
    Optional<Reservation> findById(String id);
    List<Reservation> findAll();
    
}
