package no.kristianped.springreactiverest.controllers;

import no.kristianped.springreactiverest.domain.Category;
import no.kristianped.springreactiverest.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = CategoryController.class)
class CategoryControllerTest {

    static final String BASE_URL = CategoryController.BASE_URL + "/";

    @MockBean
    CategoryRepository categoryRepository;

    @Autowired
    WebTestClient webTestClient;

    CategoryController categoryController;

    @BeforeEach
    void setUp() {
        categoryController = new CategoryController(categoryRepository);
    }

    @Test
    void list() {
        BDDMockito
                .given(categoryRepository.findAll())
                .willReturn(Flux.just(Category.builder().description("Cat").build(), Category.builder().description("Dog").build())
                );

        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    void getById() {
        BDDMockito
                .given(categoryRepository.findById(ArgumentMatchers.anyString()))
                .willReturn(Mono.just(Category.builder().description("Cat").build())
                );

        webTestClient.get().uri(BASE_URL + "hello")
                .exchange()
                .expectBody(Category.class);
    }

    @Test
    void create() {
        BDDMockito.given(categoryRepository.saveAll(ArgumentMatchers.any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().description("Cat").build()));

        Mono<Category> categoryToSave = Mono.just(Category.builder().description("Dog").build());

        webTestClient
                .post()
                .uri(BASE_URL)
                .body(categoryToSave, Category.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void update() {
        BDDMockito.given(categoryRepository.save(ArgumentMatchers.any(Category.class)))
                .willReturn(Mono.just(Category.builder().description("Cat").build()));

        Mono<Category> categoryToSave = Mono.just(Category.builder().description("Dog").build());

        webTestClient
                .put()
                .uri(BASE_URL + "sasasasasa")
                .body(categoryToSave, Category.class)
                .exchange()
                .expectStatus().isOk();


    }

    @Test
    void patch() {
        Category category = Category.builder().description("Cat").build();

        BDDMockito.given(categoryRepository.findById(ArgumentMatchers.anyString()))
                .willReturn(Mono.just(category));

        BDDMockito.given(categoryRepository.save(ArgumentMatchers.any(Category.class)))
                .willReturn(Mono.just(category));

        Mono<Category> categoryToSave = Mono.just(Category.builder().description("Dog").build());

        webTestClient
                .patch()
                .uri(BASE_URL + "sasasasasa")
                .body(categoryToSave, Category.class)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(categoryRepository).save(ArgumentMatchers.any());
    }

    @Test
    void patchNoUpdate() {
        Category category = Category.builder().description("Cat").build();

        BDDMockito.given(categoryRepository.findById(ArgumentMatchers.anyString()))
                .willReturn(Mono.just(category));

        BDDMockito.given(categoryRepository.save(ArgumentMatchers.any(Category.class)))
                .willReturn(Mono.just(category));

        Mono<Category> categoryToSave = Mono.just(category);

        webTestClient
                .patch()
                .uri(BASE_URL + "sasasasasa")
                .body(categoryToSave, Category.class)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(categoryRepository, Mockito.never()).save(ArgumentMatchers.any());
    }
}