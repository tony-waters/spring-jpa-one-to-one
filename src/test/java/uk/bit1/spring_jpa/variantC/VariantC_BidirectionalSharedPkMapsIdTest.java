package uk.bit1.spring_jpa.variantC;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariantC_BidirectionalSharedPkMapsIdTest {

    @Autowired CustomerCRepository customerRepository;
    @Autowired ProfileCRepository profileRepository;

    @Test
    void mapsId_profileSharesPrimaryKeyWithCustomer() {
        CustomerC customer = new CustomerC("Carol");
        ProfileC profile = customer.createProfile(true);

        customerRepository.saveAndFlush(customer);

        assertThat(customer.getId()).isNotNull();
        assertThat(profile.getId()).isEqualTo(customer.getId());

        ProfileC reloadedProfile = profileRepository.findById(customer.getId()).orElseThrow();
        assertThat(reloadedProfile.getCustomer().getId()).isEqualTo(customer.getId());

        CustomerC reloadedCustomer = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(reloadedCustomer.getProfile()).isNotNull();
        assertThat(reloadedCustomer.getProfile().getId()).isEqualTo(customer.getId());
    }

    @Test
    void removingProfile_triggersOrphanRemoval_andDeletesSharedPrimaryKeyRow() {
        CustomerC customer = new CustomerC("Carol");
        customer.createProfile(false);

        customerRepository.saveAndFlush(customer);
        Long sharedId = customer.getId();

        assertThat(profileRepository.findById(sharedId)).isPresent();

        CustomerC managed = customerRepository.findById(sharedId).orElseThrow();
        managed.removeProfile();
        customerRepository.saveAndFlush(managed);

        assertThat(profileRepository.findById(sharedId)).isNotPresent();
    }

    @Test
    void deletingCustomer_cascadesDeleteToSharedPrimaryKeyProfile() {
        CustomerC customer = new CustomerC("Carol");
        customer.createProfile(true);

        customerRepository.saveAndFlush(customer);
        Long sharedId = customer.getId();

        customerRepository.deleteById(sharedId);
        customerRepository.flush();

        assertThat(customerRepository.findById(sharedId)).isNotPresent();
        assertThat(profileRepository.findById(sharedId)).isNotPresent();
    }
}