package uk.bit1.spring_jpa.scenarioB;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CustomerProfileMappingDataJpaTestB {

    @Autowired
    CustomerRepositoryB customerRepository;
    @Autowired
    ProfileRepositoryB profileRepository;

    @Test
    void creatingProfilePersists() {
        CustomerB c = new CustomerB("tonyW");
        ProfileB profile = c.createProfile(true);
        ProfileB saved = profileRepository.saveAndFlush(profile);

        Long profileId = saved.getId();

        assertThat(profileId).isNotNull();

//        // Profile uses @MapsId so Profile.id == Customer.id
//        assertThat(profileRepository.findById(customerId)).isPresent();
//        ProfileB p = profileRepository.findById(customerId).orElseThrow();
//
//        assertThat(p.getId()).isEqualTo(customerId);
//        assertThat(p.isMarketingOptIn()).isTrue();
    }

//    @Test
//    void removingProfileDeletesOrphanRow() {
//        CustomerB c = new CustomerB("tonyW");
//        c.createProfile(false);
//
//        CustomerB saved = customerRepository.saveAndFlush(c);
//        Long customerId = saved.getId();
//
//        assertThat(profileRepository.findById(customerId)).isPresent();
//
//        // remove + flush => orphanRemoval should delete profile row
//        saved.removeProfile();
//        customerRepository.saveAndFlush(saved);
//
//        assertThat(profileRepository.findById(customerId)).isNotPresent();
//    }
}
