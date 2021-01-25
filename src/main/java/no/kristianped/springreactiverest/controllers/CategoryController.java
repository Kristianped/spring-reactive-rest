package no.kristianped.springreactiverest.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import no.kristianped.springreactiverest.domain.Category;
import no.kristianped.springreactiverest.repositories.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping(CategoryController.BASE_URL)
public class CategoryController {

    public static final String BASE_URL = "/api/v1/categories";

    CategoryRepository categoryRepository;

    @GetMapping
    public Flux<Category> list() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Category> getById(@PathVariable String id) {
        return categoryRepository.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<Void> create(@RequestBody Publisher<Category> categoryPublisher) {
        return categoryRepository.saveAll(categoryPublisher).then();
    }

    @PutMapping("/{id}")
    public Mono<Category> update(@PathVariable String id, @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }
}
