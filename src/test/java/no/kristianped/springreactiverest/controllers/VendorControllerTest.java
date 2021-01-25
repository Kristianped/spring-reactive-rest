package no.kristianped.springreactiverest.controllers;

import no.kristianped.springreactiverest.domain.Vendor;
import no.kristianped.springreactiverest.repositories.VendorRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = VendorController.class)
class VendorControllerTest {

    static final String BASE_URL = VendorController.BASE_URL + "/";

    @MockBean
    VendorRepository vendorRepository;

    @Autowired
    WebTestClient webTestClient;

    VendorController vendorController;

    @BeforeEach
    void setUp() {
        vendorController = new VendorController(vendorRepository);
    }

    @Test
    void list() {
        // given
        Vendor vendor1 = Vendor.builder().firstName("Michael").lastName("Weston").build();
        Vendor vendor2 = Vendor.builder().firstName("Joe").lastName("Black").build();
        List<Vendor> vendors = List.of(vendor1, vendor2);

        // when
        BDDMockito
                .given(vendorRepository.findAll())
                .willReturn(Flux.fromIterable(vendors));

        // then
        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    void getById() {
        // given
        Vendor vendor1 = Vendor.builder().firstName("Michael").lastName("Weston").build();

        // when
        BDDMockito
                .given(vendorRepository.findById(ArgumentMatchers.anyString()))
                .willReturn(Mono.just(vendor1));

        // then
        webTestClient.get().uri(BASE_URL + "hello")
                .exchange()
                .expectBody(Vendor.class)
                .value(hasProperty("firstName", equalTo("Michael")))
                .value(hasProperty("lastName", equalTo("Weston")));
    }

    @Test
    void create() {
        BDDMockito
                .given(vendorRepository.saveAll(ArgumentMatchers.any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().firstName("Hei").lastName("Deg").build()));

        Mono<Vendor> vendorToSave = Mono.just(Vendor.builder().firstName("AAAA").lastName("BBBB").build());

        webTestClient
                .post()
                .uri(BASE_URL)
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void update() {
        BDDMockito
                .given(vendorRepository.save(ArgumentMatchers.any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().firstName("Hei").lastName("Der").build()));

        Mono<Vendor> vendorToSave = Mono.just(Vendor.builder().firstName("AAAA").lastName("BBBB").build());

        webTestClient
                .put()
                .uri(BASE_URL + "aasssddf")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void patch() {
        Vendor vendor = Vendor.builder().firstName("Hei").lastName("Der").build();

        BDDMockito
                .given(vendorRepository.save(ArgumentMatchers.any(Vendor.class)))
                .willReturn(Mono.just(vendor));

        BDDMockito
                .given(vendorRepository.findById(ArgumentMatchers.anyString()))
                .willReturn(Mono.just(vendor));

        Mono<Vendor> vendorToSave = Mono.just(Vendor.builder().firstName("AAAA").lastName("BBBB").build());

        webTestClient
                .patch()
                .uri(BASE_URL + "aasssddf")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(vendorRepository).save(ArgumentMatchers.any());
    }

    @Test
    void patchNoSave() {
        Vendor vendor = Vendor.builder().firstName("Hei").lastName("Der").build();

        BDDMockito
                .given(vendorRepository.save(ArgumentMatchers.any(Vendor.class)))
                .willReturn(Mono.just(vendor));

        BDDMockito
                .given(vendorRepository.findById(ArgumentMatchers.anyString()))
                .willReturn(Mono.just(vendor));

        Mono<Vendor> vendorToSave = Mono.just(Vendor.builder().build());

        webTestClient
                .patch()
                .uri(BASE_URL + "aasssddf")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(vendorRepository, Mockito.never()).save(ArgumentMatchers.any());
    }
}