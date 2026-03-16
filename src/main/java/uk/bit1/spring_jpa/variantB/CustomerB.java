package uk.bit1.spring_jpa.variantB;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Variant B: bidirectional one-to-one with foreign key in child.
 * CustomerB is inverse side; ProfileB owns the relationship.
 */
@Entity
@Table(name = "CUSTOMER_B")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerB {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    // Inverse Side
    @OneToOne(
            fetch = FetchType.LAZY,
            mappedBy = "customer", // think 'profile.customer'
            cascade = CascadeType.ALL, // 'cascade' is on the Parent Side
            orphanRemoval = true // 'orphanRemoval' is on the Parent side
    )
    private ProfileB profile;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerB(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    // Parent side - lifecycle control lives here
    public ProfileB createProfile(boolean marketingOptIn) {
        if (this.profile != null) {
            throw new IllegalStateException("Customer already has a Profile");
        }
        this.profile = new ProfileB(marketingOptIn);
        profile.setCustomerInternal(this);
        return profile;
    }

    // Parent side - lifecycle control lives here
    public void removeProfile() {
        if (this.profile == null) {
            throw new IllegalStateException("Customer has no Profile to remove");
        }
        ProfileB old = this.profile;
        this.profile = null;
        // cascade and orphan removal takes care of Profile
    }

}
