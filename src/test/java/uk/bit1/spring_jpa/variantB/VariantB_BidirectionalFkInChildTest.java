package uk.bit1.spring_jpa.variantB;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariantB_BidirectionalFkInChildTest {

    @Autowired CustomerBRepository customerRepository;
    @Autowired ProfileBRepository profileRepository;

    @Test
    void cascadePersist_savesCustomerAndProfile_andOwnerHoldsForeignKey() {
        CustomerB customer = new CustomerB("Bob");
        ProfileB profile = customer.createProfile(true);

        customerRepository.saveAndFlush(customer);

        assertThat(customer.getId()).isNotNull();
        assertThat(profile.getId()).isNotNull();

        ProfileB reloadedProfile = profileRepository.findById(profile.getId()).orElseThrow();
        assertThat(reloadedProfile.getCustomer()).isNotNull();
        assertThat(reloadedProfile.getCustomer().getId()).isEqualTo(customer.getId());

        CustomerB reloadedCustomer = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(reloadedCustomer.getProfile()).isNotNull();
        assertThat(reloadedCustomer.getProfile().getId()).isEqualTo(profile.getId());
    }

    @Test
    void removingProfile_fromParent_triggersOrphanRemoval_andDeletesProfileRow() {
        CustomerB customer = new CustomerB("Bob");
        ProfileB profile = customer.createProfile(false);

        customerRepository.saveAndFlush(customer);
        Long profileId = profile.getId();

        assertThat(profileRepository.findById(profileId)).isPresent();

        CustomerB managed = customerRepository.findById(customer.getId()).orElseThrow();
        managed.removeProfile();
        customerRepository.saveAndFlush(managed);

        assertThat(profileRepository.findById(profileId)).isNotPresent();
    }

    @Test
    void deletingCustomer_cascadesDeleteToProfile() {
        CustomerB customer = new CustomerB("Bob");
        ProfileB profile = customer.createProfile(true);

        customerRepository.saveAndFlush(customer);
        Long customerId = customer.getId();
        Long profileId = profile.getId();

        customerRepository.deleteById(customerId);
        customerRepository.flush();

        assertThat(customerRepository.findById(customerId)).isNotPresent();
        assertThat(profileRepository.findById(profileId)).isNotPresent();
    }
}