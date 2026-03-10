package uk.bit1.spring_jpa.hibernate;

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
class CustomerB_ProfileLazyLoadingObservationTest {

    @Autowired
    CustomerBRepository customerRepository;
    @Autowired EntityManager entityManager;

    @Test
    @Transactional
    void profileIsObservedAsLazyInThisHibernateSetup() {
        CustomerB customer = new CustomerB("Bob");
        customer.createProfile(true);
        customerRepository.saveAndFlush(customer);

        entityManager.clear();

        CustomerB loaded = customerRepository.findById(customer.getId()).orElseThrow();

        assertThat(Hibernate.isInitialized(loaded.getProfile()))
                .as("Observed Hibernate behaviour: profile is not initialized yet")
                .isFalse();

        boolean marketingOptIn = loaded.getProfile().isMarketingOptIn();

        assertThat(marketingOptIn).isTrue();
        assertThat(Hibernate.isInitialized(loaded.getProfile()))
                .as("Observed Hibernate behaviour: accessing profile initializes it")
                .isTrue();
    }
}