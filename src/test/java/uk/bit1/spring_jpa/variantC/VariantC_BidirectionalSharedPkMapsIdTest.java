package uk.bit1.spring_jpa.variantC;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.bit1.spring_jpa.support.SchemaAssertion;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariantC_BidirectionalSharedPkMapsIdTest {

    @Autowired CustomerCRepository customerRepository;
    @Autowired ProfileCRepository profileRepository;
    @Autowired JdbcTemplate jdbc;

    @Test
    void savingCustomer_cascadesPersistToProfile_andAssignsSharedIdentifier() {
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
    void removingProfileFromCustomer_orphansAndDeletesSharedPrimaryKeyRow() {
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

    @Test
    void persistedProfileRow_usesSameIdentifierAsCustomer() {
        CustomerC customer = new CustomerC("Carol");
        ProfileC profile = customer.createProfile(true);

        customerRepository.saveAndFlush(customer);

        Long storedId = jdbc.queryForObject(
                "select customer_id from profile_c where customer_id = ?",
                Long.class,
                customer.getId()
        );

        assertThat(storedId).isEqualTo(customer.getId());
        assertThat(storedId).isEqualTo(profile.getId());
    }

    @Test
    void removeThenCreate_assignsSameSharedIdentifierToNewProfile() {
        CustomerC customer = new CustomerC("Carol");
        ProfileC first = customer.createProfile(true);
        customerRepository.saveAndFlush(customer);

        Long sharedId = customer.getId();
        assertThat(first.getId()).isEqualTo(sharedId);

        CustomerC managed = customerRepository.findById(sharedId).orElseThrow();
        managed.removeProfile();
        customerRepository.saveAndFlush(managed);

        assertThat(profileRepository.findById(sharedId)).isNotPresent();

        managed.createProfile(false);
        customerRepository.saveAndFlush(managed);

        CustomerC reloaded = customerRepository.findById(sharedId).orElseThrow();
        assertThat(reloaded.getProfile()).isNotNull();
        assertThat(reloaded.getProfile().getId()).isEqualTo(sharedId);
        assertThat(reloaded.getProfile().isMarketingOptIn()).isFalse();
    }

    @Test
    void schema_profileTableDoesNotHaveSeparateIdColumn() {
        assertThat(SchemaAssertion.columnExists(jdbc, "profile_c", "id")).isFalse();
        assertThat(SchemaAssertion.columnExists(jdbc, "profile_c", "customer_id")).isTrue();
    }

    @Test
    void schema_customerIdIsPrimaryKeyColumnOfProfileTable() {
        Integer count = jdbc.queryForObject("""
            select count(*)
            from information_schema.table_constraints tc
            join information_schema.key_column_usage kcu
              on tc.constraint_name = kcu.constraint_name
             and tc.table_schema = kcu.table_schema
             and tc.table_name = kcu.table_name
            where tc.constraint_type = 'PRIMARY KEY'
              and upper(tc.table_name) = 'PROFILE_C'
              and upper(kcu.column_name) = 'CUSTOMER_ID'
            """, Integer.class);

        assertThat(count).isGreaterThan(0);
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
              and upper(tc.table_name) = 'PROFILE_C'
              and upper(kcu.column_name) = 'CUSTOMER_ID'
            """, Integer.class);

        assertThat(count).isGreaterThan(0);
    }

}