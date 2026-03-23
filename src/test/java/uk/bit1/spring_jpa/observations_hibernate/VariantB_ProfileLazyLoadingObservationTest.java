package uk.bit1.spring_jpa.observations_hibernate;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import uk.bit1.spring_jpa.variantB.CustomerB;
import uk.bit1.spring_jpa.variantB.CustomerBRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariantB_ProfileLazyLoadingObservationTest {

    @Autowired CustomerBRepository customerRepository;
    @Autowired EntityManager entityManager;

    @Test
    void profileIsObservedAsEagerInThisHibernateSetup() {
        CustomerB customer = new CustomerB("Bob");
        customer.createProfile(true);
        customerRepository.saveAndFlush(customer);

        entityManager.clear();

        CustomerB loaded = customerRepository.findById(customer.getId()).orElseThrow();

        assertThat(Hibernate.isInitialized(loaded.getProfile()))
                .as("Observed Hibernate behaviour: inverse-side one-to-one is already initialized")
                .isTrue();

        assertThat(loaded.getProfile().isMarketingOptIn()).isTrue();
    }
}