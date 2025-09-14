package repository.inmemory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import modal.Reservation;
import repository.ReservationRepository;

public class InMemoryReservationRepository implements ReservationRepository {
    
    private final Map<String, Reservation> reservations = new HashMap<>();

    @Override
    public void save(Reservation reservation) {
        reservations.put(reservation.getId(), reservation);
    }

    @Override
    public void update(Reservation reservation) {
        reservations.put(reservation.getId(), reservation);
    }

    @Override
    public List<Reservation> findActiveQueue(String isbn, String branchId) {
        return reservations.values().stream()
                .filter(r -> r.getIsbn().equals(isbn) && r.getBranchId().equals(branchId) && r.isActive() && !r.isNotified())
                .sorted(Comparator.comparing(Reservation::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(reservations.get(id));
    }

    @Override
    public List<Reservation> findAll() {
        return List.copyOf(reservations.values());
    }
}