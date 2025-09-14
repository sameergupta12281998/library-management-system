package serivces;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import modal.Patron;
import modal.Reservation;
import notification.Notifier;
import notification.NotifierFactory;
import repository.PatronRepository;
import repository.ReservationRepository;

public class ReservationService {
    
    private static final Logger log = Logger.getLogger(ReservationService.class.getName());
    private final ReservationRepository reservationRepo;
    private final PatronRepository patronRepo;
    private final Inventory inventory;
    private final Notifier notifier;

     public ReservationService(ReservationRepository reservationRepo,
                              PatronRepository patronRepo,
                              Inventory inventory,
                              NotifierFactory.Channel channel){
        this.reservationRepo = reservationRepo;
        this.patronRepo = patronRepo;
        this.inventory = inventory;
        this.notifier = NotifierFactory.create(channel);
    }

    public Reservation reserve(String isbn, String patronId, String branchId){
        Reservation r = new Reservation(isbn, patronId, branchId);
        reservationRepo.save(r);
        log.info(() -> "Created reservation " + r);
        return r;
    }


    public void onCopyAvailable(String isbn, String branchId){
        if (inventory.getAvailabeCount(branchId, isbn) <= 0) return;
        List<Reservation> queue = reservationRepo.findActiveQueue(isbn, branchId);
        if (queue.isEmpty()) return;

        Reservation next = queue.get(0);
        Optional<Patron> patron = patronRepo.findById(next.getPatronId());
        patron.ifPresent(p -> notifier.notify(
                p.getEmail() != null ? p.getEmail() : p.getPhoneNumber(),
                "Your reserved book (ISBN: " + isbn + ") is now available at branch " + branchId + "."
        ));
        next.markNotified();
        reservationRepo.update(next);
        log.info(() -> "Notified reservation " + next.getId() + " for isbn=" + isbn);
    }

    public void cancel(String reservationId){
        reservationRepo.findById(reservationId).ifPresent(r -> {
            r.cancel();
            reservationRepo.update(r);
            log.info(() -> "Cancelled reservation " + r);
        });
    }
}
