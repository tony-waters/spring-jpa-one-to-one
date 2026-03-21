package uk.bit1.spring_jpa.variantD;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VariantD_EntityContractTest {

    @Test
    void createProfile_whenProfileAlreadyExists_throws() {
        CustomerD customer = new CustomerD("Dan");
        customer.createProfile(true);

        assertThatThrownBy(() -> customer.createProfile(false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Customer already has a Profile");
    }

    @Test
    void removeProfile_whenNoProfileExists_throws() {
        CustomerD customer = new CustomerD("Dan");

        assertThatThrownBy(customer::removeProfile)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Customer has no Profile to remove");
    }

}