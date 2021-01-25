package no.kristianped.springreactiverest.repositories;

import no.kristianped.springreactiverest.domain.Vendor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface VendorRepository extends ReactiveMongoRepository<Vendor, String> {
}
