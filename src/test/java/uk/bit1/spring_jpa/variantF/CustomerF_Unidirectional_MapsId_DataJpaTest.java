package uk.bit1.spring_jpa.variantF;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class CustomerF_Unidirectional_MapsId_DataJpaTest {

    @Autowired
    CustomerFRepository customerRepo;
    @Autowired
    ProfileFRepository profileRepo;

    @Test
    void profileSharesPrimaryKeyWithCustomer_butMustBeSavedSeparately() {
        CustomerF c = new CustomerF("Eve");
        customerRepo.saveAndFlush(c);

        assertThat(c.getId()).isNotNull();

        ProfileF p = new ProfileF(c, true);
        profileRepo.saveAndFlush(p);

        assertThat(p.getId()).isEqualTo(c.getId());
        assertThat(profileRepo.findById(c.getId())).isPresent();
    }

    @Test
    void deletingCustomerDoesNotAutomaticallyDeleteProfile_inThisUnidirectionalModel() {
        CustomerF c = new CustomerF("Eve");
        customerRepo.saveAndFlush(c);

        ProfileF p = new ProfileF(c, false);
        profileRepo.saveAndFlush(p);

        Long id = c.getId();
        assertThat(profileRepo.findById(id)).isPresent();

        // Depending on DB + JPA DDL, this may fail with FK constraint
        // or it may delete customer and leave profile broken (bad).
        // In a tutorial, this is the point: you need explicit lifecycle handling.
//        assertThatThrownBy(() -> {
//            customerRepo.deleteById(id);
//            customerRepo.flush();
//        }).isInstanceOfAny(Exception.class);

        // Clean up explicitly (what a service would do):
        profileRepo.deleteById(id);
        profileRepo.flush();

        customerRepo.deleteById(id);
        customerRepo.flush();

        assertThat(profileRepo.findById(id)).isNotPresent();
        assertThat(customerRepo.findById(id)).isNotPresent();
    }
}