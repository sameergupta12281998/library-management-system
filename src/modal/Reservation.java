package modal;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    
     private final String id;
    private final String isbn;
    private final String patronId;
    private final String branchId;
    private final LocalDateTime createdAt;
    private boolean active;
    private boolean notified;

    public Reservation(String isbn, String patronId, String branchId){
        this(UUID.randomUUID().toString(), isbn, patronId, branchId, LocalDateTime.now(), true, false);
    }

    public Reservation(String id, String isbn, String patronId, String branchId, LocalDateTime createdAt, boolean active, boolean notified){
        this.id = id;
        this.isbn = isbn;
        this.patronId = patronId;
        this.branchId = branchId;
        this.createdAt = createdAt;
        this.active = active;
        this.notified = notified;
    }

    public String getId(){ return id; }
    public String getIsbn(){ return isbn; }
    public String getPatronId(){ return patronId; }
    public String getBranchId(){ return branchId; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public boolean isActive(){ return active; }
    public boolean isNotified(){ return notified; }

    public void cancel(){ this.active = false; }
    public void markNotified(){ this.notified = true; }

    @Override
    public String toString(){
        return "Reservation{id='%s', isbn='%s', patronId='%s', active=%s, notified=%s}".formatted(id, isbn, patronId, active, notified);
    }
}
