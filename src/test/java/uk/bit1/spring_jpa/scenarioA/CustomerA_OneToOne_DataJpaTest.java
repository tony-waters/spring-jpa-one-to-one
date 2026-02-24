package uk.bit1.spring_jpa.scenarioA;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class CustomerA_OneToOne_DataJpaTest {

    @Autowired CustomerARepository customerRepo;
    @Autowired ProfileARepository profileRepo;

    @Test
    void cascadePersistsProfile_andBidirectionalNavigationWorks() {
        CustomerA c = new CustomerA("Alice");
        ProfileA p = c.createProfile(true);

        customerRepo.saveAndFlush(c);

        assertThat(c.getId()).isNotNull();
        assertThat(p.getId()).isNotNull();

        CustomerA reloadedCustomer = customerRepo.findById(c.getId()).orElseThrow();
        assertThat(reloadedCustomer.getProfile()).isNotNull();
        assertThat(reloadedCustomer.getProfile().isMarketingOptIn()).isTrue();

        ProfileA reloadedProfile = profileRepo.findById(p.getId()).orElseThrow();
        assertThat(reloadedProfile.getCustomer()).isNotNull();
        assertThat(reloadedProfile.getCustomer().getId()).isEqualTo(c.getId());
    }

    @Test
    void orphanRemovalDeletesProfileRow() {
        CustomerA c = new CustomerA("Alice");
        ProfileA p = c.createProfile(false);

        customerRepo.saveAndFlush(c);
        Long profileId = p.getId();
        assertThat(profileId).isNotNull();
        assertThat(profileRepo.findById(profileId)).isPresent();

        CustomerA managed = customerRepo.findById(c.getId()).orElseThrow();
        managed.removeProfile();
        customerRepo.saveAndFlush(managed);

        assertThat(profileRepo.findById(profileId)).isNotPresent();
    }
}