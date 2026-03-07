package uk.bit1.spring_jpa.scenarioD;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_c")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileD {

    @Id
    @Getter
    private Long id; // no @GeneratedValue — comes from Customer via @MapsId

    // Owning side
    @OneToOne(optional = false) // TODO: avoid the eager fetching?
    @MapsId
    @JoinColumn(
            name = "customer_id",
            nullable = false,
            unique = true
    )
    @Getter(AccessLevel.PROTECTED)
    private CustomerD customer;

    @Getter
    @Column(nullable = false)
    private boolean marketingOptIn = false;

    ProfileD(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }

    void setCustomerInternal(CustomerD customer) {
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
        this.id = null; // keeps in-memory state coherent; JPA will delete via orphanRemoval anyway
    }

}

