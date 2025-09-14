package serivces;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import modal.Book;
import recommendation.RecommendationStrategy;

public class RecommendationService {
    private static final Logger log = Logger.getLogger(RecommendationService.class.getName());
    private RecommendationStrategy strategy;

    public RecommendationService(RecommendationStrategy strategy){
        this.strategy = strategy;
    }

    public void setStrategy(RecommendationStrategy strategy){
        this.strategy = Objects.requireNonNull(strategy);
        log.info(() -> "Switched recommendation strategy to " + strategy.name());
    }

    public List<Book> recommend(String patronId, int k){
        if (strategy == null) return Collections.emptyList();
        return strategy.recommend(patronId, k);
    }
}
