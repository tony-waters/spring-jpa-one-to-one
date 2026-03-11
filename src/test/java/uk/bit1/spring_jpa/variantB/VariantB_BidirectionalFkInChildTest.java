package uk.bit1.spring_jpa.variantB;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.bit1.spring_jpa.support.SchemaAssertion;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariantB_BidirectionalFkInChildTest {

    @Autowired CustomerBRepository customerRepository;
    @Autowired ProfileBRepository profileRepository;
    @Autowired JdbcTemplate jdbc;


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

    @Test
    void schema_fkColumnExistsOnProfileTable_notCustomerTable() {
        assertThat(SchemaAssertion.columnExists(jdbc, "profile_b", "customer_id")).isTrue();
        assertThat(SchemaAssertion.columnExists(jdbc, "customer_b", "profile_id")).isFalse();
    }

    @Test
    void schema_customerFkInProfile_isUnique() {
        assertThat(SchemaAssertion.uniqueConstraintExistsForColumn(jdbc, "profile_b", "customer_id")).isTrue();
    }

    @Test
    void persistedRows_storeFkOnProfileRow() {
        CustomerB customer = new CustomerB("Bob");
        ProfileB profile = customer.createProfile(true);
        customerRepository.saveAndFlush(customer);

        Long fkValue = jdbc.queryForObject(
                "select customer_id from profile_b where id = ?",
                Long.class,
                profile.getId()
        );

        assertThat(fkValue).isEqualTo(customer.getId());
    }

}