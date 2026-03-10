package uk.bit1.spring_jpa.variantD;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariantD_DataJpaTest {

    @Autowired CustomerDRepository customerRepo;
    @Autowired ProfileDRepository profileRepo;

    @Test
    void cascadePersistsProfile_withoutBackReference() {
        CustomerD c = new CustomerD("Dan");
        ProfileD p = c.createProfile(true);

        customerRepo.saveAndFlush(c);

        assertThat(c.getId()).isNotNull();
        assertThat(p.getId()).isNotNull();

        CustomerD reloaded = customerRepo.findById(c.getId()).orElseThrow();
        assertThat(reloaded.getProfile()).isNotNull();
        assertThat(reloaded.getProfile().isMarketingOptIn()).isTrue();
    }

    @Test
    void orphanRemovalDeletesProfileRow() {
        CustomerD c = new CustomerD("Dan");
        ProfileD p = c.createProfile(false);

        customerRepo.saveAndFlush(c);
        Long profileId = p.getId();
        assertThat(profileRepo.findById(profileId)).isPresent();

        CustomerD managed = customerRepo.findById(c.getId()).orElseThrow();
        managed.removeProfile();
        customerRepo.saveAndFlush(managed);

        assertThat(profileRepo.findById(profileId)).isNotPresent();
    }
}