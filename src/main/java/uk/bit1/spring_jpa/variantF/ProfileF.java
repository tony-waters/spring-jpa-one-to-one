package uk.bit1.spring_jpa.variantF;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PROFILE_F")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileF {

    @Id
    @Getter
    private Long id; // no @GeneratedValue — comes from Customer via @MapsId

    @OneToOne(
            optional = false // child must always reference a parent
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

    // no lifecycle management
    // relationship is managed from the Service layer
}

