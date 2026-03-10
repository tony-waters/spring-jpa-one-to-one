package uk.bit1.spring_jpa.variantE;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ScenarioE_DataJpaTest {

    @Autowired
    CustomerERepository customerRepository;

    @Autowired
    ProfileERepository profileRepository;

    @Test
    void savesProfileWithCustomer() {
        CustomerE customer = customerRepository.saveAndFlush(new CustomerE("Alice"));

        ProfileE profile = new ProfileE(customer, true);
        profile = profileRepository.saveAndFlush(profile);

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getCustomer()).isNotNull();
        assertThat(profile.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(profile.isMarketingOptIn()).isTrue();
    }

    @Test
    void uniqueConstraintPreventsTwoProfilesForSameCustomer() {
        CustomerE customer = customerRepository.saveAndFlush(new CustomerE("Alice"));

        ProfileE first = new ProfileE(customer, true);
        profileRepository.saveAndFlush(first);

        ProfileE second = new ProfileE(customer, false);

        assertThatThrownBy(() -> profileRepository.saveAndFlush(second))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void customerCanExistWithoutProfile() {
        CustomerE customer = customerRepository.saveAndFlush(new CustomerE("Alice"));

        assertThat(customer.getId()).isNotNull();
        assertThat(profileRepository.findAll()).isEmpty();
    }
}