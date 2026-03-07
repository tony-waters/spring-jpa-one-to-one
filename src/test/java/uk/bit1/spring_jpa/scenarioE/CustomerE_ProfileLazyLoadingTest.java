package uk.bit1.spring_jpa.scenarioE;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerE_ProfileLazyLoadingTest {

    @Autowired
    CustomerERepository customerRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @Transactional
    void profileIsLoadedLazily() {

        // given
        CustomerE c = new CustomerE("Alice");
        c.createProfile(true);
        customerRepository.saveAndFlush(c);

        entityManager.clear(); // detach everything

        // when
        CustomerE loaded = customerRepository.findById(c.getId()).orElseThrow();

        // then
        assertThat(Hibernate.isInitialized(loaded.getProfile()))
                .as("Profile should NOT be initialized yet")
                .isFalse();

        // accessing it should initialize it
        boolean marketingOptIn = loaded.getProfile().isMarketingOptIn();

        assertThat(Hibernate.isInitialized(loaded.getProfile()))
                .as("Profile should now be initialized")
                .isTrue();

        assertThat(marketingOptIn).isEqualTo(true);
    }
}