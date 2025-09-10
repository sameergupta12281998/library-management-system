package repository;

import java.util.List;
import java.util.Optional;

import modal.Loan;

public interface LoanRepository {
    
    void save(Loan loan);
    void update(Loan loan);
    Optional<Loan> findById(String id);
    List<Loan> findPatronById(String partonID);
    List<Loan> findActiveLoansByISBNAndBranch(String isbn, String branchID);
    List<Loan> findAll();
}
