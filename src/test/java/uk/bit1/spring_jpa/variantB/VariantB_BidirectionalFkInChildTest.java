package uk.bit1.spring_jpa.variantB;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.bit1.spring_jpa.support.SchemaAssertion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class VariantB_BidirectionalFkInChildTest {

    @Autowired CustomerBRepository customerRepository;
    @Autowired ProfileBRepository profileRepository;
    @Autowired JdbcTemplate jdbc;

    @Test
    void savingCustomer_cascadesPersistToProfile_andPreservesBidirectionalLinks() {
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
    void removingProfileFromCustomer_orphansAndDeletesProfile() {
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
    void replacingProfile_orphansAndDeletesOldProfile() {
        CustomerB customer = new CustomerB("Bob");
        ProfileB first = customer.createProfile(true);
        customerRepository.saveAndFlush(customer);

        Long customerId = customer.getId();
        Long firstId = first.getId();

        CustomerB managed = customerRepository.findById(customerId).orElseThrow();
        managed.removeProfile();
        managed.createProfile(false);
        customerRepository.saveAndFlush(managed);

        assertThat(profileRepository.findById(firstId)).isNotPresent();

        CustomerB reloaded = customerRepository.findById(customerId).orElseThrow();
        assertThat(reloaded.getProfile()).isNotNull();
        assertThat(reloaded.getProfile().getId()).isNotNull();
        assertThat(reloaded.getProfile().getId()).isNotEqualTo(firstId);
        assertThat(reloaded.getProfile().isMarketingOptIn()).isFalse();
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

    @Test
    void databaseUniqueConstraint_preventsTwoProfilesForSameCustomer() {
        CustomerB customer = new CustomerB("Bob");
        customer.createProfile(true);
        customerRepository.saveAndFlush(customer);

        CustomerB managedCustomer = customerRepository.findById(customer.getId()).orElseThrow();

        ProfileB second = new ProfileB(false);
        second.setCustomerInternal(managedCustomer);

        assertThatThrownBy(() -> profileRepository.saveAndFlush(second))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

    @Test
    void schema_fkColumnExistsOnProfileTable_notCustomerTable() {
        assertThat(SchemaAssertion.columnExists(jdbc, "profile_b", "customer_id")).isTrue();
        assertThat(SchemaAssertion.columnExists(jdbc, "customer_b", "profile_id")).isFalse();
    }

    @Test
    void schema_profileCustomerId_isUnique() {
        assertThat(SchemaAssertion.uniqueConstraintExistsForColumn(jdbc, "profile_b", "customer_id")).isTrue();
    }

    @Test
    void schema_foreignKeyExistsOnProfileCustomerId() {
        Integer count = jdbc.queryForObject("""
            select count(*)
            from information_schema.table_constraints tc
            join information_schema.key_column_usage kcu
              on tc.constraint_name = kcu.constraint_name
             and tc.table_schema = kcu.table_schema
             and tc.table_name = kcu.table_name
            where tc.constraint_type = 'FOREIGN KEY'
              and upper(tc.table_name) = 'PROFILE_B'
              and upper(kcu.column_name) = 'CUSTOMER_ID'
            """, Integer.class);

        assertThat(count).isGreaterThan(0);
    }
}