package uk.bit1.spring_jpa.entity_1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CustomerProfileMappingDataJpaTest {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProfileRepository profileRepository;

    @Test
    void creatingProfilePersistsAndSharesPrimaryKey() {
        Customer c = new Customer("tonyW");
        c.createProfile(true);

        Customer saved = customerRepository.saveAndFlush(c);
        Long customerId = saved.getId();

        assertThat(customerId).isNotNull();

        // Profile uses @MapsId so Profile.id == Customer.id
        assertThat(profileRepository.findById(customerId)).isPresent();
        Profile p = profileRepository.findById(customerId).orElseThrow();

        assertThat(p.getId()).isEqualTo(customerId);
        assertThat(p.isMarketingOptIn()).isTrue();
    }

    @Test
    void removingProfileDeletesOrphanRow() {
        Customer c = new Customer("tonyW");
        c.createProfile(false);

        Customer saved = customerRepository.saveAndFlush(c);
        Long customerId = saved.getId();

        assertThat(profileRepository.findById(customerId)).isPresent();

        // remove + flush => orphanRemoval should delete profile row
        saved.removeProfile();
        customerRepository.saveAndFlush(saved);

        assertThat(profileRepository.findById(customerId)).isNotPresent();
    }
}
