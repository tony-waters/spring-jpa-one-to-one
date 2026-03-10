package uk.bit1.spring_jpa.variantD;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerD_ProfileLazyLoadingObservationTest {

    @Autowired CustomerDRepository customerRepository;
    @Autowired EntityManager entityManager;

    @Test
    @Transactional
    void profileIsObservedAsLazyInThisHibernateSetup() {
        CustomerD customer = new CustomerD("Dan");
        customer.createProfile(true);
        customerRepository.saveAndFlush(customer);

        entityManager.clear();

        CustomerD loaded = customerRepository.findById(customer.getId()).orElseThrow();

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