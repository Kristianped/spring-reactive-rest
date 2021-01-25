package no.kristianped.springreactiverest.bootstrap;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import no.kristianped.springreactiverest.domain.Category;
import no.kristianped.springreactiverest.domain.Vendor;
import no.kristianped.springreactiverest.repositories.CategoryRepository;
import no.kristianped.springreactiverest.repositories.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class Bootstrap implements CommandLineRunner {

    CategoryRepository categoryRepository;
    VendorRepository vendorRepository;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        log.debug("Loading Bootstrap data");

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(null, "Fruits"));
        categories.add(new Category(null, "Nuts"));
        categories.add(new Category(null, "Breads"));
        categories.add(new Category(null, "Meats"));
        categories.add(new Category(null, "Eggs"));

        categoryRepository.deleteAll()
                .thenMany(Flux.fromIterable(categories).flatMap(categoryRepository::save))
                .subscribe(category -> log.debug(category + " saved"));

        vendorRepository
                .deleteAll()
                .thenMany(
                        Flux.just(
                                Vendor.builder().firstName("Joe").lastName("Buck").build(),
                                Vendor.builder().firstName("Michael").lastName("Weston").build(),
                                Vendor.builder().firstName("Jessie").lastName("Waters").build(),
                                Vendor.builder().firstName("Jimmy").lastName("Buffet").build()
                        ).flatMap(vendorRepository::save))
                .subscribe(vendor -> log.debug(vendor + " saved"));
    }
}
