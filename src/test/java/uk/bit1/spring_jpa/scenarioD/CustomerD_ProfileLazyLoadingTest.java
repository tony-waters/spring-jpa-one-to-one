package uk.bit1.spring_jpa.scenarioD;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import uk.bit1.spring_jpa.scenarioC.CustomerC;
import uk.bit1.spring_jpa.scenarioC.CustomerCRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerD_ProfileLazyLoadingTest {

    @Autowired
    CustomerDRepository customerRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @Transactional
    void profile_is_not_loaded_when_customer_is_loaded() {

        // given
        CustomerD c = new CustomerD("Alice");
        c.createProfile(true);
        customerRepository.saveAndFlush(c);

        entityManager.clear(); // detach everything

        // when
        CustomerD loaded = customerRepository.findById(c.getId()).orElseThrow();

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