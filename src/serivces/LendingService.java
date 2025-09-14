package serivces;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import modal.Loan;
import repository.LoanRepository;

public class LendingService {
    
    private static final Logger log = Logger.getLogger(LendingService.class.getName());
    private final LoanRepository loanRepo;
    private final Inventory inventory;

    public LendingService(LoanRepository loanRepo, Inventory inventory) {
        this.loanRepo = loanRepo;
        this.inventory = inventory;
    }

    public Optional<Loan> chechout(String isbn, String patronId, String branchId,int loanDays){
        boolean ok = inventory.checkout(branchId, isbn);
        if(!ok){
            log.warning(() -> "Checkout failed for ISBN " + isbn + " at branch " + branchId);
            return Optional.empty();
        }

        LocalDate checkoutDate = LocalDate.now();
        LocalDate dueDate = checkoutDate.plusDays(loanDays);
        Loan loan = new Loan(patronId, isbn, branchId, checkoutDate, dueDate);
        loanRepo.save(loan);
        log.info(() -> "Book checked out: " + loan);
        return Optional.of(loan);
    }

    public boolean returnBook(String loanId){
        Optional<Loan> loanOpt = loanRepo.findById(loanId);
        if(loanOpt.isEmpty()) return false;

        Loan loan = loanOpt.get();
        if(loan.isBookReturned()) return false;
    
        loan.markReturned(LocalDate.now());
        loanRepo.update(loan);
        inventory.checkin(loan.getBranchID(), loan.getIsbn());
        log.info(() -> "Book returned: " + loan);
        return true;
    }

    public List<Loan> borrowingHistory(String patronId){
        return loanRepo.findPatronById(patronId);
    }
}
