package repository;

import java.util.List;
import java.util.Optional;

import modal.Patron;

public interface PatronRepository {

    void save(Patron patron);
    void update(Patron patron);
    Optional<Patron> findById(String id);
    List<Patron> findAll();
    void deleteById(String id);

}
