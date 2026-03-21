package uk.bit1.spring_jpa.variantF;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.bit1.spring_jpa.support.SchemaAssertion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class VariantF_UnidirectionalSharedPkExplicitLifecycleTest {

    @Autowired CustomerFRepository customerRepository;
    @Autowired ProfileFRepository profileRepository;
    @Autowired JdbcTemplate jdbc;
    @PersistenceContext EntityManager entityManager;

    @Test
    void savingProfileAfterPersistingCustomer_assignsSharedIdentifier() {
        CustomerF customer = customerRepository.saveAndFlush(new CustomerF("Eve"));

        ProfileF profile = new ProfileF(customer, true);
        profileRepository.saveAndFlush(profile);

        assertThat(customer.getId()).isNotNull();
        assertThat(profile.getId()).isEqualTo(customer.getId());
        assertThat(profileRepository.findById(customer.getId())).isPresent();
    }

    @Test
    void deletingProfileThenCustomer_succeedsWhenDoneInCorrectOrder() {
        CustomerF customer = customerRepository.saveAndFlush(new CustomerF("Eve"));
        profileRepository.saveAndFlush(new ProfileF(customer, false));

        Long sharedId = customer.getId();

        profileRepository.deleteById(sharedId);
        profileRepository.flush();

        customerRepository.deleteById(sharedId);
        customerRepository.flush();

        assertThat(profileRepository.findById(sharedId)).isNotPresent();
        assertThat(customerRepository.findById(sharedId)).isNotPresent();
    }

    @Test
    void persistedProfileRow_usesSameIdentifierAsCustomer() {
        CustomerF customer = customerRepository.saveAndFlush(new CustomerF("Eve"));
        ProfileF profile = profileRepository.saveAndFlush(new ProfileF(customer, true));

        Long storedId = jdbc.queryForObject(
                "select customer_id from profile_f where customer_id = ?",
                Long.class,
                customer.getId()
        );

        assertThat(storedId).isEqualTo(customer.getId());
        assertThat(storedId).isEqualTo(profile.getId());
    }

    @Test
    void deletingManagedCustomerWhileManagedProfileStillReferencesIt_failsAtOrmLevel() {
        CustomerF customer = customerRepository.saveAndFlush(new CustomerF("Eve"));
        profileRepository.saveAndFlush(new ProfileF(customer, true));

        assertThatThrownBy(() -> {
            customerRepository.delete(customer);
            customerRepository.flush();
        }).isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void deletingCustomerRowBeforeProfileRow_failsWithConstraintViolationAtDatabaseLevel() {
        CustomerF customer = customerRepository.saveAndFlush(new CustomerF("Eve"));
        profileRepository.saveAndFlush(new ProfileF(customer, true));

        Long customerId = customer.getId();

        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() -> {
            customerRepository.deleteById(customerId);
            customerRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void secondProfileForSameCustomer_failsAndLeavesOriginalProfileIntact() {
        CustomerF customer = customerRepository.saveAndFlush(new CustomerF("Eve"));
        ProfileF first = profileRepository.saveAndFlush(new ProfileF(customer, true));

        ProfileF second = new ProfileF(customer, false);

        assertThatThrownBy(() -> profileRepository.saveAndFlush(second))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(profileRepository.findById(first.getId())).isPresent();
    }

    @Test
    void schema_profileTableDoesNotHaveSeparateIdColumn() {
        assertThat(SchemaAssertion.columnExists(jdbc, "profile_f", "id")).isFalse();
        assertThat(SchemaAssertion.columnExists(jdbc, "profile_f", "customer_id")).isTrue();
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
          and upper(tc.table_name) = 'PROFILE_F'
          and upper(kcu.column_name) = 'CUSTOMER_ID'
        """, Integer.class);

        assertThat(count).isGreaterThan(0);
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
          and upper(tc.table_name) = 'PROFILE_F'
          and upper(kcu.column_name) = 'CUSTOMER_ID'
        """, Integer.class);

        assertThat(count).isGreaterThan(0);
    }

}