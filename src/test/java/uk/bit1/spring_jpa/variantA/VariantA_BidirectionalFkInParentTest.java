package uk.bit1.spring_jpa.variantA;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.bit1.spring_jpa.support.SchemaAssertion;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariantA_BidirectionalFkInParentTest {

    @Autowired CustomerARepository customerRepository;
    @Autowired ProfileARepository profileRepository;
    @Autowired JdbcTemplate jdbc;

    @Test
    void savingCustomer_cascadesPersistToProfile_andPreservesBidirectionalLinks() {
        CustomerA customer = new CustomerA("Alice");
        ProfileA profile = customer.createProfile(true);

        customerRepository.saveAndFlush(customer);

        assertThat(customer.getId()).isNotNull();
        assertThat(profile.getId()).isNotNull();

        CustomerA reloadedCustomer = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(reloadedCustomer.getProfile()).isNotNull();
        assertThat(reloadedCustomer.getProfile().isMarketingOptIn()).isTrue();

        ProfileA reloadedProfile = profileRepository.findById(profile.getId()).orElseThrow();
        assertThat(reloadedProfile.getCustomer()).isNotNull();
        assertThat(reloadedProfile.getCustomer().getId()).isEqualTo(customer.getId());
    }

    @Test
    void removingProfileFromCustomer_orphansAndDeletesProfile() {
        CustomerA customer = new CustomerA("Alice");
        ProfileA profile = customer.createProfile(false);

        customerRepository.saveAndFlush(customer);
        Long profileId = profile.getId();

        assertThat(profileRepository.findById(profileId)).isPresent();

        CustomerA managed = customerRepository.findById(customer.getId()).orElseThrow();
        managed.removeProfile();
        customerRepository.saveAndFlush(managed);

        assertThat(profileRepository.findById(profileId)).isNotPresent();
    }

    @Test
    void deletingCustomer_cascadesDeleteToProfile() {
        CustomerA customer = new CustomerA("Alice");
        ProfileA profile = customer.createProfile(true);

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
        CustomerA customer = new CustomerA("Alice");
        ProfileA first = customer.createProfile(true);
        customerRepository.saveAndFlush(customer);

        Long customerId = customer.getId();
        Long firstId = first.getId();

        CustomerA managed = customerRepository.findById(customerId).orElseThrow();
        managed.removeProfile();
        managed.createProfile(false);
        customerRepository.saveAndFlush(managed);

        assertThat(profileRepository.findById(firstId)).isNotPresent();

        CustomerA reloaded = customerRepository.findById(customerId).orElseThrow();
        assertThat(reloaded.getProfile()).isNotNull();
        assertThat(reloaded.getProfile().getId()).isNotNull();
        assertThat(reloaded.getProfile().getId()).isNotEqualTo(firstId);
        assertThat(reloaded.getProfile().isMarketingOptIn()).isFalse();
    }

    @Test
    void persistedRows_storeFkOnCustomerRow() {
        CustomerA customer = new CustomerA("Alice");
        ProfileA profile = customer.createProfile(true);
        customerRepository.saveAndFlush(customer);

        Long fkValue = jdbc.queryForObject(
                "select profile_id from customer_a where id = ?",
                Long.class,
                customer.getId()
        );

        assertThat(fkValue).isEqualTo(profile.getId());
    }

    @Test
    void schema_fkColumnExistsOnCustomerTable_notProfileTable() {
        assertThat(SchemaAssertion.columnExists(jdbc, "customer_a", "profile_id")).isTrue();
        assertThat(SchemaAssertion.columnExists(jdbc, "profile_a", "customer_id")).isFalse();
    }

    @Test
    void schema_customerProfileId_isUnique() {
        assertThat(SchemaAssertion.uniqueConstraintExistsForColumn(jdbc, "customer_a", "profile_id")).isTrue();
    }

    @Test
    void schema_foreignKeyExistsOnCustomerProfileId() {
        Integer count = jdbc.queryForObject("""
            select count(*)
            from information_schema.table_constraints tc
            join information_schema.key_column_usage kcu
              on tc.constraint_name = kcu.constraint_name
             and tc.table_schema = kcu.table_schema
             and tc.table_name = kcu.table_name
            where tc.constraint_type = 'FOREIGN KEY'
              and upper(tc.table_name) = 'CUSTOMER_A'
              and upper(kcu.column_name) = 'PROFILE_ID'
            """, Integer.class);

        assertThat(count).isGreaterThan(0);
    }

}