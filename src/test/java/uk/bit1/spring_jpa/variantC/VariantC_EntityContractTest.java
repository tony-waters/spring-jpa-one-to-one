package uk.bit1.spring_jpa.variantC;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VariantC_EntityContractTest {

    @Test
    void createProfile_whenProfileAlreadyExists_throws() {
        CustomerC customer = new CustomerC("Carol");
        customer.createProfile(true);

        assertThatThrownBy(() -> customer.createProfile(false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Customer already has a Profile");
    }

    @Test
    void removeProfile_whenNoProfileExists_throws() {
        CustomerC customer = new CustomerC("Carol");

        assertThatThrownBy(customer::removeProfile)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Customer has no Profile to remove");
    }

    @Test
    void profileCannotBeMovedToAnotherCustomer() {
        CustomerC first = new CustomerC("Carol");
        CustomerC second = new CustomerC("Dave");
        ProfileC profile = new ProfileC(true);

        profile.setCustomerInternal(first);

        assertThatThrownBy(() -> profile.setCustomerInternal(second))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Profile cannot be moved to another Customer");
    }
}