package uk.bit1.spring_jpa.variantB;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PROFILE_B")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileB {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Long id;

    // Owning side: FK lives in 'profile' table (customer_id)
    @OneToOne(
            optional = false
    )
    @JoinColumn( // Owner side is here
            name = "customer_id",
            nullable = false,
            unique = true // enforce 1-1 relationship
    )
    @Getter(AccessLevel.PROTECTED)
    private CustomerB customer;

    @Getter
    @Column(nullable = false)
    private boolean marketingOptIn = false;

    ProfileB(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }

    void setCustomerInternal(CustomerB customer) {
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

