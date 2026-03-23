package uk.bit1.spring_jpa.variantF;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Variant F: unidirectional one-to-one with shared primary key via @MapsId.
 * CustomerF has no reference to ProfileF; lifecycle ordering is handled by the caller.
 */
@Entity
@Table(name = "profile_f")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileF {

    @Id
    @Getter
    private Long id;

    @OneToOne(
            optional = false
    )
    @MapsId
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    @Getter
    private CustomerF customer;

    @Getter
    @Column(nullable = false)
    private boolean marketingOptIn = false;

    public ProfileF(CustomerF customer, boolean marketingOptIn) {
        if (customer == null) {
            throw new IllegalArgumentException("customer must not be null");
        }
        this.customer = customer;
        this.marketingOptIn = marketingOptIn;
    }

    // No bidirectional helper methods.
    // Caller must persist and delete in the correct order.
    // (this would be pushed to the Service layer)
}

