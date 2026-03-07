package uk.bit1.spring_jpa.scenarioB;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerB_OneToOne_DataJpaTest {

    @Autowired CustomerBRepository customerRepo;
    @Autowired ProfileBRepository profileRepo;

    @Test
    void cascadePersistsProfile_andProfileHoldsUniqueCustomerFk() {
        CustomerB c = new CustomerB("Bob");
        ProfileB p = c.createProfile(true);

        customerRepo.saveAndFlush(c);

        assertThat(c.getId()).isNotNull();
        assertThat(p.getId()).isNotNull();

        ProfileB reloadedProfile = profileRepo.findById(p.getId()).orElseThrow();
        assertThat(reloadedProfile.getCustomer()).isNotNull();
        assertThat(reloadedProfile.getCustomer().getId()).isEqualTo(c.getId());

        CustomerB reloadedCustomer = customerRepo.findById(c.getId()).orElseThrow();
        assertThat(reloadedCustomer.getProfile()).isNotNull();
        assertThat(reloadedCustomer.getProfile().getId()).isEqualTo(p.getId());
    }

    @Test
    void orphanRemovalDeletesProfileRow_whenCustomerDropsReference() {
        CustomerB c = new CustomerB("Bob");
        ProfileB p = c.createProfile(false);

        customerRepo.saveAndFlush(c);
        Long profileId = p.getId();
        assertThat(profileRepo.findById(profileId)).isPresent();

        CustomerB managed = customerRepo.findById(c.getId()).orElseThrow();
        managed.removeProfile();
        customerRepo.saveAndFlush(managed);

        assertThat(profileRepo.findById(profileId)).isNotPresent();
    }
}