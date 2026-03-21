package uk.bit1.spring_jpa.variantA;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VariantA_EntityContractTest {

    @Test
    void createProfile_whenProfileAlreadyExists_throws() {
        CustomerA customer = new CustomerA("Alice");
        customer.createProfile(true);

        assertThatThrownBy(() -> customer.createProfile(false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Customer already has a Profile");
    }

    @Test
    void removeProfile_whenNoProfileExists_throws() {
        CustomerA customer = new CustomerA("Alice");

        assertThatThrownBy(customer::removeProfile)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Customer has no Profile to remove");
    }

}