package repository.inmemory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import modal.Branch;
import repository.BranchRepository;

public class InMemoryBranchRepository implements BranchRepository{

      private final Logger log = Logger.getLogger(InMemoryBranchRepository.class.getName());
      private final Map<String, Branch> branches = new HashMap<>();

    @Override
    public void save(Branch branch) {
        branches.put(branch.getId(), branch);
        log.fine(() -> "Saved branch " + branch);
    }

    @Override
    public void update(Branch branch) {
        branches.put(branch.getId(), branch);
        log.fine(() -> "Updated branch " + branch);
    }

    @Override
    public Optional<Branch> findById(String id) {
        return Optional.ofNullable(branches.get(id));
    }

    @Override
    public List<Branch> findAll() {
        return List.copyOf(branches.values());
    }
    
}
