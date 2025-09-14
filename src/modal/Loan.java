package modal;

import java.time.LocalDate;
import java.util.UUID;

public class Loan {

    private final String id;
    private final String patronID;
    private final String isbn;
    private final String branchID;
    private final LocalDate checkout;
    private final LocalDate dueDate;
    private LocalDate returnDate;

     public Loan(String patronID, String isbn, String branchID, LocalDate checkout, LocalDate dueDate) {
        this(UUID.randomUUID().toString(), patronID, isbn, branchID, checkout, dueDate, null);
    }

    public Loan(String id, String patronID, String isbn, String branchID, LocalDate checkout, LocalDate dueDate,
            LocalDate returnDate) {
        this.id = id;
        this.patronID = patronID;
        this.isbn = isbn;
        this.branchID = branchID;
        this.checkout = checkout;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public String getId() {
        return id;
    }
    public String getPatronID() {
        return patronID;
    }
    public String getIsbn() {
        return isbn;
    }
    public String getBranchID() {
        return branchID;
    }
    public LocalDate getCheckout() {
        return checkout;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public LocalDate getReturnDate() {
        return returnDate;
    }
    public boolean isBookReturned() {
        return returnDate != null;
    }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "Loan{id='%s', patronID='%s', isbn='%s', branchID='%s', checkout=%s, dueDate=%s, returnDate=%s}"
                .formatted(id, patronID, isbn, branchID, checkout, dueDate, returnDate);
    }
    
}
