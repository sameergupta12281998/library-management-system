package repository;

import java.util.List;
import java.util.Optional;

import modal.Branch;

public interface BranchRepository {

    void save(Branch branch);
    void update(Branch branch);
    Optional<Branch> findById(String id);
    List<Branch> findAll();
    
}
