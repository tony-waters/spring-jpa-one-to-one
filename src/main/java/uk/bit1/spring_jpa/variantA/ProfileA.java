package uk.bit1.spring_jpa.variantA;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Variant A: bidirectional one-to-one with foreign key in parent.
 * CustomerA owns the relationship; ProfileA is inverse side.
 */
@Entity
@Table(name = "profile_a")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileA {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Long id;

    @OneToOne(
            mappedBy = "profile"
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
        if (this.customer != null && this.customer != customer) {
            throw new IllegalStateException("Profile cannot be moved to another Customer");
        }
        this.customer = customer;
    }

    void clearCustomerInternal() {
        this.customer = null;
    }

}

