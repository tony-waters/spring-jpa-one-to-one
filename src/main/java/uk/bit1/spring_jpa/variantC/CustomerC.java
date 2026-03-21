package uk.bit1.spring_jpa.variantC;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Variant C: bidirectional one-to-one with shared primary key via @MapsId.
 * CustomerC is inverse side; ProfileC owns the relationship and shares the identifier.
 */
@Entity
@Table(name = "customer_c")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerC {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    @OneToOne(
            mappedBy = "customer",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ProfileC profile;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerC(String displayName) {
        String normalized = (displayName == null ? null : displayName.strip());
        if (normalized == null || normalized.isEmpty()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = normalized;
    }

    public ProfileC createProfile(boolean marketingOptIn) {
        if (this.profile != null) {
            throw new IllegalStateException("Customer already has a Profile");
        }
        ProfileC profile = new ProfileC(marketingOptIn);
        profile.setCustomerInternal(this);
        this.profile = profile;
        return profile;
    }

    public void removeProfile() {
        if (this.profile == null) {
            throw new IllegalStateException("Customer has no Profile to remove");
        }
        ProfileC old = this.profile;
        this.profile = null;
        old.clearCustomerInternal();
    }
}