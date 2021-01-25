package no.kristianped.springreactiverest.repositories;

import no.kristianped.springreactiverest.domain.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
}
