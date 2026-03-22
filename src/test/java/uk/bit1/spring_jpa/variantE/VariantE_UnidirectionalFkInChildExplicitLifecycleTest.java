package uk.bit1.spring_jpa.variantE;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class VariantE_UnidirectionalFkInChildExplicitLifecycleTest {

    @Autowired CustomerERepository customerRepository;
    @Autowired ProfileERepository profileRepository;
    @Autowired JdbcTemplate jdbc;
    @PersistenceContext EntityManager entityManager;

    @Test
    void savingProfileAfterPersistingCustomer_succeeds() {
        CustomerE customer = customerRepository.saveAndFlush(new CustomerE("Alice"));

        ProfileE profile = new ProfileE(customer, true);
        profileRepository.saveAndFlush(profile);

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getCustomer()).isNotNull();
        assertThat(profile.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(profile.isMarketingOptIn()).isTrue();
    }

    @Test
    void secondProfileForSameCustomer_failsAndLeavesOriginalProfileIntact() {
        CustomerE customer = customerRepository.saveAndFlush(new CustomerE("Alice"));
        ProfileE first = profileRepository.saveAndFlush(new ProfileE(customer, true));

        ProfileE second = new ProfileE(customer, false);

        assertThatThrownBy(() -> profileRepository.saveAndFlush(second))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(profileRepository.findById(first.getId())).isPresent();
    }

    @Test
    void customerCanExistWithoutProfile() {
        CustomerE customer = customerRepository.saveAndFlush(new CustomerE("Alice"));

        assertThat(customer.getId()).isNotNull();
        assertThat(profileRepository.findAll()).isEmpty();
    }

    @Test
    void deletingManagedCustomerWhileManagedProfileStillReferencesIt_failsAtOrmLevel() {
        CustomerE customer = customerRepository.saveAndFlush(new CustomerE("Alice"));
        profileRepository.saveAndFlush(new ProfileE(customer, true));

        assertThatThrownBy(() -> {
            customerRepository.delete(customer);
            customerRepository.flush();
        }).isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void deletingCustomerRowBeforeProfileRow_failsWithConstraintViolationAtDatabaseLevel() {
        CustomerE customer = customerRepository.saveAndFlush(new CustomerE("Alice"));
        profileRepository.saveAndFlush(new ProfileE(customer, true));

        Long customerId = customer.getId();

        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() -> {
            customerRepository.deleteById(customerId);
            customerRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void savingProfileWithTransientCustomer_failsBecauseParentMustBePersistedFirst() {
        CustomerE customer = new CustomerE("Alice");
        ProfileE profile = new ProfileE(customer, true);

        assertThatThrownBy(() -> profileRepository.saveAndFlush(profile))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void schema_foreignKeyExistsFromProfileToCustomer() {
        Integer count = jdbc.queryForObject("""
            select count(*)
            from information_schema.table_constraints tc
            join information_schema.key_column_usage kcu
              on tc.constraint_name = kcu.constraint_name
             and tc.table_schema = kcu.table_schema
             and tc.table_name = kcu.table_name
            where tc.constraint_type = 'FOREIGN KEY'
              and upper(tc.table_name) = 'PROFILE_E'
              and upper(kcu.column_name) = 'CUSTOMER_ID'
            """, Integer.class);

        assertThat(count).isGreaterThan(0);
    }

    @Test
    void schema_fkColumnExistsOnProfileTable_notCustomerTable() {
        Integer profileCount = jdbc.queryForObject("""
            select count(*)
            from information_schema.columns
            where upper(table_name) = 'PROFILE_E'
              and upper(column_name) = 'CUSTOMER_ID'
            """, Integer.class);

        Integer customerCount = jdbc.queryForObject("""
            select count(*)
            from information_schema.columns
            where upper(table_name) = 'CUSTOMER_E'
              and upper(column_name) = 'PROFILE_ID'
            """, Integer.class);

        assertThat(profileCount).isEqualTo(1);
        assertThat(customerCount).isEqualTo(0);
    }

    @Test
    void persistedRows_storeFkOnProfileRow() {
        CustomerE customer = customerRepository.saveAndFlush(new CustomerE("Alice"));
        ProfileE profile = profileRepository.saveAndFlush(new ProfileE(customer, true));

        Long fkValue = jdbc.queryForObject(
                "select customer_id from profile_e where id = ?",
                Long.class,
                profile.getId()
        );

        assertThat(fkValue).isEqualTo(customer.getId());
    }
}