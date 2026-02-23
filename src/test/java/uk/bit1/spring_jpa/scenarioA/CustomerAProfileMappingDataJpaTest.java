package uk.bit1.spring_jpa.scenarioA;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CustomerAProfileMappingDataJpaTest {

    @Autowired
    CustomerRepositoryA customerRepository;
    @Autowired
    ProfileRepositoryA profileRepository;

    @Test
    void creatingProfilePersistsAndSharesPrimaryKey() {
        CustomerA c = new CustomerA("tonyW");
        c.createProfile(true);

        CustomerA saved = customerRepository.saveAndFlush(c);
        Long customerId = saved.getId();

        assertThat(customerId).isNotNull();

        // Profile uses @MapsId so Profile.id == Customer.id
        assertThat(profileRepository.findById(customerId)).isPresent();
        ProfileA p = profileRepository.findById(customerId).orElseThrow();

        assertThat(p.getId()).isEqualTo(customerId);
        assertThat(p.isMarketingOptIn()).isTrue();
    }

    @Test
    void removingProfileDeletesOrphanRow() {
        CustomerA c = new CustomerA("tonyW");
        c.createProfile(false);

        CustomerA saved = customerRepository.saveAndFlush(c);
        Long customerId = saved.getId();

        assertThat(profileRepository.findById(customerId)).isPresent();

        // remove + flush => orphanRemoval should delete profile row
        saved.removeProfile();
        customerRepository.saveAndFlush(saved);

        assertThat(profileRepository.findById(customerId)).isNotPresent();
    }
}
