package uk.bit1.spring_jpa.scenarioE;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerE_Unidirectional_DataJpaTest {

    @Autowired CustomerERepository customerRepo;
    @Autowired ProfileERepository profileRepo;

    @Test
    void cascadePersistsProfile_withoutBackReference() {
        CustomerE c = new CustomerE("Dan");
        ProfileE p = c.createProfile(true);

        customerRepo.saveAndFlush(c);

        assertThat(c.getId()).isNotNull();
        assertThat(p.getId()).isNotNull();

        CustomerE reloaded = customerRepo.findById(c.getId()).orElseThrow();
        assertThat(reloaded.getProfile()).isNotNull();
        assertThat(reloaded.getProfile().isMarketingOptIn()).isTrue();
    }

    @Test
    void orphanRemovalDeletesProfileRow() {
        CustomerE c = new CustomerE("Dan");
        ProfileE p = c.createProfile(false);

        customerRepo.saveAndFlush(c);
        Long profileId = p.getId();
        assertThat(profileRepo.findById(profileId)).isPresent();

        CustomerE managed = customerRepo.findById(c.getId()).orElseThrow();
        managed.removeProfile();
        customerRepo.saveAndFlush(managed);

        assertThat(profileRepo.findById(profileId)).isNotPresent();
    }
}