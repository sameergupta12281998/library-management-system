package repository.inmemory;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import modal.Loan;
import repository.LoanRepository;

public class InMemoryLoanRepository implements LoanRepository {

    private final Map<String, Loan> loans = new HashMap<>();

    @Override
    public void save(Loan loan) {
        loans.put(loan.getId(), loan);
    }

    @Override
    public void update(Loan loan) {
        loans.put(loan.getId(), loan);
    }

    @Override
    public Optional<Loan> findById(String id) {
        return Optional.ofNullable(loans.get(id));
    }

    @Override
    public List<Loan> findPatronById(String partonID) {
        return loans.values().stream()
                .filter(loan -> loan.getPatronID().equals(partonID))
                .sorted(Comparator.comparing(Loan::getCheckout).reversed())
                .collect(Collectors.toList());
         }

    @Override
    public List<Loan> findActiveLoansByISBNAndBranch(String isbn, String branchID) {
        return loans.values().stream()
                .filter(loan -> loan.getIsbn().equals(isbn) && loan.getBranchID().equals(branchID) && !loan.isBookReturned())
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findAll() {
        return List.copyOf(loans.values());
    }
    
}
