package uk.bit1.spring_jpa.variantC;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PROFILE_C")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileC {

    @Id
    @Getter
    private Long id; // no @GeneratedValue — comes from Customer via @MapsId

    // Owning side
    @OneToOne(optional = false) // child must always reference a parent
    @MapsId
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    @Getter(AccessLevel.PROTECTED)
    private CustomerC customer;

    @Getter
    @Column(nullable = false)
    private boolean marketingOptIn = false;

    ProfileC(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }

    void setCustomerInternal(CustomerC customer) {
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
        // Clearing the child reference keeps the in-memory object graph coherent.
        // The actual database row is removed by JPA because of 'orphanRemoval = true'
        this.id = null;
    }

}

