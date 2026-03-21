package uk.bit1.spring_jpa.variantE;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Variant E: unidirectional one-to-one with foreign key in child.
 * ProfileE owns the relationship and references CustomerE directly.
 */
@Entity
@Table(name = "profile_e")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileE {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false,
            unique = true
    )
    @Getter(AccessLevel.PROTECTED)
    private CustomerE customer;

    @Getter
    @Column(nullable = false)
    private boolean marketingOptIn = false;

    ProfileE(CustomerE customer, boolean marketingOptIn) {
        if (customer == null) {
            throw new IllegalArgumentException("customer must not be null");
        }
        this.customer = customer;
        this.marketingOptIn = marketingOptIn;
    }
}