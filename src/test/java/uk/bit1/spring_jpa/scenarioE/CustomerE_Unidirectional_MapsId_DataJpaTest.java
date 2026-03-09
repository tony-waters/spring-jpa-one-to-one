package uk.bit1.spring_jpa.scenarioE;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class CustomerE_Unidirectional_MapsId_DataJpaTest {

    @Autowired CustomerERepository customerRepo;
    @Autowired ProfileERepository profileRepo;

    @Test
    void profileSharesPrimaryKeyWithCustomer_butMustBeSavedSeparately() {
        CustomerE c = new CustomerE("Eve");
        customerRepo.saveAndFlush(c);

        assertThat(c.getId()).isNotNull();

        ProfileE p = new ProfileE(c, true);
        profileRepo.saveAndFlush(p);

        assertThat(p.getId()).isEqualTo(c.getId());
        assertThat(profileRepo.findById(c.getId())).isPresent();
    }

    @Test
    void deletingCustomerDoesNotAutomaticallyDeleteProfile_inThisUnidirectionalModel() {
        CustomerE c = new CustomerE("Eve");
        customerRepo.saveAndFlush(c);

        ProfileE p = new ProfileE(c, false);
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