package uk.bit1.spring_jpa.scenarioD;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerD_MapsId_DataJpaTest {

    @Autowired CustomerDRepository customerRepo;
    @Autowired ProfileDRepository profileRepo;

    @Test
    void profileSharesPrimaryKeyWithCustomer() {
        CustomerD c = new CustomerD("Carol");
        ProfileD p = c.createProfile(true);

        customerRepo.saveAndFlush(c);

        assertThat(c.getId()).isNotNull();
        assertThat(p.getId()).isNotNull();

        // The point of @MapsId:
        assertThat(p.getId()).isEqualTo(c.getId());

        ProfileD reloadedProfile = profileRepo.findById(c.getId()).orElseThrow();
        assertThat(reloadedProfile.getCustomer().getId()).isEqualTo(c.getId());

        CustomerD reloadedCustomer = customerRepo.findById(c.getId()).orElseThrow();
        assertThat(reloadedCustomer.getProfile().getId()).isEqualTo(c.getId());
    }

    @Test
    void orphanRemovalDeletesProfileRow() {
        CustomerD c = new CustomerD("Carol");
        c.createProfile(false);

        customerRepo.saveAndFlush(c);
        Long sharedId = c.getId();

        assertThat(profileRepo.findById(sharedId)).isPresent();

        CustomerD managed = customerRepo.findById(sharedId).orElseThrow();
        managed.removeProfile();
        customerRepo.saveAndFlush(managed);

        assertThat(profileRepo.findById(sharedId)).isNotPresent();
    }
}