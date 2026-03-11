package uk.bit1.spring_jpa.hibernate;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import uk.bit1.spring_jpa.variantB.CustomerBRepository;
import uk.bit1.spring_jpa.variantB.ProfileBRepository;
import uk.bit1.spring_jpa.variantF.CustomerF;
import uk.bit1.spring_jpa.variantF.CustomerFRepository;
import uk.bit1.spring_jpa.variantF.ProfileF;
import uk.bit1.spring_jpa.variantF.ProfileFRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class VariantF_SharedPkHibernateObservationTest {

    @Autowired CustomerFRepository customerRepository;
    @Autowired ProfileFRepository profileRepository;

    @Test
    void hibernateMayPersistTransientParentWhenSavingMapsIdChild_inThisSetup() {
        // In this setup, Hibernate chooses to persist the referenced parent automatically
        // so it can satisfy the derived identity relationship.
        // ... this is not guaranteed for other setups
        CustomerF transientCustomer = new CustomerF("Eve");
        ProfileF profile = new ProfileF(transientCustomer, false);

        profileRepository.saveAndFlush(profile);

        assertThat(transientCustomer.getId()).isNotNull();
        assertThat(profile.getId()).isEqualTo(transientCustomer.getId());
        assertThat(customerRepository.findById(transientCustomer.getId())).isPresent();
        assertThat(profileRepository.findById(transientCustomer.getId())).isPresent();
    }
}
