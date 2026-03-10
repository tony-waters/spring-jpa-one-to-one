package uk.bit1.spring_jpa.variantC;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class VariantC_DataJpaTest {

    @Autowired CustomerCRepository customerRepo;
    @Autowired ProfileCRepository profileRepo;

    @Test
    void profileSharesPrimaryKeyWithCustomer() {
        CustomerC c = new CustomerC("Carol");
        ProfileC p = c.createProfile(true);

        customerRepo.saveAndFlush(c);

        assertThat(c.getId()).isNotNull();
        assertThat(p.getId()).isNotNull();

        // The point of @MapsId:
        assertThat(p.getId()).isEqualTo(c.getId());

        ProfileC reloadedProfile = profileRepo.findById(c.getId()).orElseThrow();
        assertThat(reloadedProfile.getCustomer().getId()).isEqualTo(c.getId());

        CustomerC reloadedCustomer = customerRepo.findById(c.getId()).orElseThrow();
        assertThat(reloadedCustomer.getProfile().getId()).isEqualTo(c.getId());
    }

    @Test
    void orphanRemovalDeletesProfileRow() {
        CustomerC c = new CustomerC("Carol");
        c.createProfile(false);

        customerRepo.saveAndFlush(c);
        Long sharedId = c.getId();

        assertThat(profileRepo.findById(sharedId)).isPresent();

        CustomerC managed = customerRepo.findById(sharedId).orElseThrow();
        managed.removeProfile();
        customerRepo.saveAndFlush(managed);

        assertThat(profileRepo.findById(sharedId)).isNotPresent();
    }
}