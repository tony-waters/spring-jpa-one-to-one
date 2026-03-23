package uk.bit1.spring_jpa.observations_hibernate;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import uk.bit1.spring_jpa.variantC.CustomerC;
import uk.bit1.spring_jpa.variantC.CustomerCRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariantC_ProfileLazyLoadingObservationTest {

    @Autowired CustomerCRepository customerRepository;
    @Autowired EntityManager entityManager;

    @Test
    void profileIsObservedAsEagerInThisHibernateSetup() {
        CustomerC customer = new CustomerC("Carol");
        customer.createProfile(true);
        customerRepository.saveAndFlush(customer);

        entityManager.clear();

        CustomerC loaded = customerRepository.findById(customer.getId()).orElseThrow();

        assertThat(Hibernate.isInitialized(loaded.getProfile()))
                .as("Observed Hibernate behaviour: shared-PK inverse-side one-to-one is already initialized")
                .isTrue();
    }
}