package uk.bit1.spring_jpa.variantB;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VariantB_EntityContractTest {

    @Test
    void createProfile_whenProfileAlreadyExists_throws() {
        CustomerB customer = new CustomerB("Bob");
        customer.createProfile(true);

        assertThatThrownBy(() -> customer.createProfile(false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Customer already has a Profile");
    }

    @Test
    void removeProfile_whenNoProfileExists_throws() {
        CustomerB customer = new CustomerB("Bob");

        assertThatThrownBy(customer::removeProfile)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Customer has no Profile to remove");
    }
}