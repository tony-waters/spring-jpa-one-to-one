package uk.bit1.spring_jpa.variantD;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.bit1.spring_jpa.support.SchemaAssertion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class VariantD_UnidirectionalFkInParentTest {

    @Autowired CustomerDRepository customerRepository;
    @Autowired ProfileDRepository profileRepository;
    @Autowired JdbcTemplate jdbc;

    @Test
    void savingCustomer_cascadesPersistToProfile_withoutBackReference() {
        CustomerD customer = new CustomerD("Dan");
        ProfileD profile = customer.createProfile(true);

        customerRepository.saveAndFlush(customer);

        assertThat(customer.getId()).isNotNull();
        assertThat(profile.getId()).isNotNull();

        CustomerD reloadedCustomer = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(reloadedCustomer.getProfile()).isNotNull();
        assertThat(reloadedCustomer.getProfile().isMarketingOptIn()).isTrue();
    }

    @Test
    void removingProfileFromCustomer_orphansAndDeletesProfile() {
        CustomerD customer = new CustomerD("Dan");
        ProfileD profile = customer.createProfile(false);

        customerRepository.saveAndFlush(customer);
        Long profileId = profile.getId();

        assertThat(profileRepository.findById(profileId)).isPresent();

        CustomerD managed = customerRepository.findById(customer.getId()).orElseThrow();
        managed.removeProfile();
        customerRepository.saveAndFlush(managed);

        assertThat(profileRepository.findById(profileId)).isNotPresent();
    }

    @Test
    void deletingCustomer_cascadesDeleteToProfile() {
        CustomerD customer = new CustomerD("Dan");
        ProfileD profile = customer.createProfile(true);

        customerRepository.saveAndFlush(customer);
        Long customerId = customer.getId();
        Long profileId = profile.getId();

        customerRepository.deleteById(customerId);
        customerRepository.flush();

        assertThat(customerRepository.findById(customerId)).isNotPresent();
        assertThat(profileRepository.findById(profileId)).isNotPresent();
    }

    @Test
    void persistedRows_storeFkOnCustomerRow() {
        CustomerD customer = new CustomerD("Dan");
        ProfileD profile = customer.createProfile(true);

        customerRepository.saveAndFlush(customer);

        Long fkValue = jdbc.queryForObject(
                "select profile_id from customer_d where id = ?",
                Long.class,
                customer.getId()
        );

        assertThat(fkValue).isEqualTo(profile.getId());
    }

    @Test
    void databaseUniqueConstraint_preventsReusingSameProfileForTwoCustomers() {
        CustomerD firstCustomer = new CustomerD("Dan");
        ProfileD sharedProfile = firstCustomer.createProfile(true);
        customerRepository.saveAndFlush(firstCustomer);

        CustomerD secondCustomer = new CustomerD("Dave");
        secondCustomer.attachProfile(sharedProfile);

        assertThatThrownBy(() -> customerRepository.saveAndFlush(secondCustomer))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(customerRepository.findById(firstCustomer.getId())).isPresent();
        assertThat(profileRepository.findById(sharedProfile.getId())).isPresent();
    }

    @Test
    void schema_fkColumnExistsOnCustomerTable_notProfileTable() {
        assertThat(SchemaAssertion.columnExists(jdbc, "customer_d", "profile_id")).isTrue();
        assertThat(SchemaAssertion.columnExists(jdbc, "profile_d", "customer_id")).isFalse();
    }

    @Test
    void schema_customerProfileId_isUnique() {
        assertThat(SchemaAssertion.uniqueConstraintExistsForColumn(jdbc, "customer_d", "profile_id")).isTrue();
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
              and upper(tc.table_name) = 'CUSTOMER_D'
              and upper(kcu.column_name) = 'PROFILE_ID'
            """, Integer.class);

        assertThat(count).isGreaterThan(0);
    }

}