package repository.inmemory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import modal.Patron;
import repository.PatronRepository;

public class InMemoryPartonRepository  implements PatronRepository {

    private final Logger log = Logger.getLogger(InMemoryPartonRepository.class.getName());
     private final Map<String, Patron> patrons = new HashMap<>();


    @Override
    public void save(Patron patron) {
        patrons.put(patron.getId(), patron);
        log.fine(() -> "Saved patron " + patron);
    }

    @Override
    public void update(Patron patron) {
        patrons.put(patron.getId(), patron);
        log.fine(() -> "Updated patron " + patron);
    }

    @Override
    public List<Patron> findAll() {
        return List.copyOf(patrons.values());
    }


    @Override
    public Optional<Patron> findById(String id) {
        return Optional.ofNullable(patrons.get(id));
    }

    @Override
    public void deleteById(String id) {
        patrons.remove(id);
        log.fine(() -> "Deleted patron with ID " + id);
    }
    
}
