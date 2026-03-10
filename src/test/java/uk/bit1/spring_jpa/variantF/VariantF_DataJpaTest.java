package uk.bit1.spring_jpa.variantF;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class VariantF_DataJpaTest {

    @Autowired CustomerFRepository customerRepository;
    @Autowired ProfileFRepository profileRepository;

    @Test
    void mapsId_profileSharesPrimaryKeyWithCustomer_whenSavedSeparately() {
        CustomerF customer = customerRepository.saveAndFlush(new CustomerF("Eve"));

        ProfileF profile = new ProfileF(customer, true);
        profileRepository.saveAndFlush(profile);

        assertThat(customer.getId()).isNotNull();
        assertThat(profile.getId()).isEqualTo(customer.getId());
        assertThat(profileRepository.findById(customer.getId())).isPresent();
    }

    @Test
    void savingProfileWithoutPersistedCustomerFails() {
        CustomerF transientCustomer = new CustomerF("Eve");
        ProfileF profile = new ProfileF(transientCustomer, false);

        assertThatThrownBy(() -> profileRepository.saveAndFlush(profile))
                .isInstanceOfAny(DataIntegrityViolationException.class, RuntimeException.class);
    }

    @Test
    void explicitDeleteOfProfileThenCustomerWorksInServiceManagedModel() {
        CustomerF customer = customerRepository.saveAndFlush(new CustomerF("Eve"));
        ProfileF profile = profileRepository.saveAndFlush(new ProfileF(customer, false));

        Long sharedId = customer.getId();

        profileRepository.deleteById(sharedId);
        profileRepository.flush();

        customerRepository.deleteById(sharedId);
        customerRepository.flush();

        assertThat(profileRepository.findById(sharedId)).isNotPresent();
        assertThat(customerRepository.findById(sharedId)).isNotPresent();
    }
}