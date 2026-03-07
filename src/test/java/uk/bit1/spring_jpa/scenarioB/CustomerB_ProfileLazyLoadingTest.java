package uk.bit1.spring_jpa.scenarioB;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerB_ProfileLazyLoadingTest {

    @Autowired
    CustomerBRepository customerRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @Transactional
    void profile_is_not_loaded_when_customer_is_loaded() {

        // given
        CustomerB c = new CustomerB("Alice");
        c.createProfile(true);
        customerRepository.saveAndFlush(c);

        entityManager.clear(); // detach everything

        // when
        CustomerB loaded = customerRepository.findById(c.getId()).orElseThrow();

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