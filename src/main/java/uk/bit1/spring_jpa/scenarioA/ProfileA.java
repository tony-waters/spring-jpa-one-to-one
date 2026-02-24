package uk.bit1.spring_jpa.scenarioA;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Profile is the Inverse side
// Profile is the Child side
// relationship is bidirectional

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileA {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Long id;

    // Inverse side
    @OneToOne(
            mappedBy = "profile" // think 'customer.profile'
    )
    @Getter(AccessLevel.PROTECTED)
    private CustomerA customer;

    @Getter
    @Column(nullable = false)
    private boolean marketingOptIn = false;

    ProfileA(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }

    void setCustomerInternal(CustomerA customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Profile must have a Customer");
        }
        if (this.customer != null && !this.customer.equals(customer)) {
            throw new IllegalStateException("Profile cannot be moved to another Customer");
        }
        this.customer = customer;
    }

    void clearCustomerInternal() {
        this.customer = null;
    }

}

