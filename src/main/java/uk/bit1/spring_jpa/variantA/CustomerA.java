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
@Table(name = "customer_a")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerA {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(
            name = "profile_id",
            unique = true
    )
    private ProfileA profile;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerA(String displayName) {
        String normalized = (displayName == null ? null : displayName.strip());
        if (normalized == null || normalized.isEmpty()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = normalized;
    }

    public ProfileA createProfile(boolean marketingOptIn) {
        if (this.profile != null) {
            throw new IllegalStateException("Customer already has a Profile");
        }
        ProfileA profile = new ProfileA(marketingOptIn);
        profile.setCustomerInternal(this);
        this.profile = profile;
        return profile;
    }

    public void removeProfile() {
        if (this.profile == null) {
            throw new IllegalStateException("Customer has no Profile to remove");
        }
        ProfileA old =  this.profile;
        this.profile = null;
        old.clearCustomerInternal();
    }

}
