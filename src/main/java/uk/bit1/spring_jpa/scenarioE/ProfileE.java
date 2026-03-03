package uk.bit1.spring_jpa.scenarioE;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_e")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileE {

    @Id
    @Getter
    private Long id; // no @GeneratedValue — comes from Customer via @MapsId

    @OneToOne(
            optional = false // TODO: avoids the eager fetching?
    )
    @MapsId
    @JoinColumn(
            name = "customer_id",
            nullable = false,
            unique = true
    )
    @Getter
    private CustomerE customer;

    @Getter
    @Column(nullable = false)
    private boolean marketingOptIn = false;

    public ProfileE(CustomerE customer, boolean marketingOptIn) {
        if (customer == null) {
            throw new IllegalArgumentException("customer must not be null");
        }
        this.customer = customer;
        this.marketingOptIn = marketingOptIn;
    }

    // no lifecycle management for Customer
}

