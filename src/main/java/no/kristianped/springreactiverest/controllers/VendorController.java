package no.kristianped.springreactiverest.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import no.kristianped.springreactiverest.domain.Vendor;
import no.kristianped.springreactiverest.repositories.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping(VendorController.BASE_URL)
public class VendorController {

    public static final String BASE_URL = "/api/v1/vendors";

    VendorRepository vendorRepository;

    @GetMapping
    public Flux<Vendor> list() {
        return vendorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Vendor> getById(@PathVariable String id) {
        return vendorRepository.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<Void> create(@RequestBody Publisher<Vendor> publisher) {
        return vendorRepository.saveAll(publisher).then();
    }

    @PutMapping("/{id}")
    public Mono<Vendor> update(@PathVariable String id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @PatchMapping("/{id}")
    public Mono<Vendor> patch(@PathVariable String id, @RequestBody Vendor vendor) {
        Mono<Vendor> vendorMono = vendorRepository.findById(id);

        return vendorMono
                .filter(foundVendor -> checkIfCanPatch(vendor, foundVendor))
                .flatMap(foundVendor -> {
                    if (vendor.getFirstName() != null && !Objects.equals(foundVendor.getFirstName(), vendor.getFirstName()))
                        foundVendor.setFirstName(vendor.getFirstName());

                    if (vendor.getLastName() != null && !Objects.equals(foundVendor.getLastName(), vendor.getLastName()))
                        foundVendor.setLastName(vendor.getLastName());

                    return vendorRepository.save(foundVendor);
                }).switchIfEmpty(vendorMono);
    }

    private boolean checkIfCanPatch(Vendor vendor, Vendor foundVendor) {
        if (vendor.getFirstName() != null && !Objects.equals(foundVendor.getFirstName(), vendor.getFirstName()))
            return true;

        if (vendor.getLastName() != null && !Objects.equals(foundVendor.getLastName(), vendor.getLastName()))
            return true;

        return false;
    }
}
