package uk.bit1.spring_jpa.variantD;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Variant D: unidirectional one-to-one with foreign key in parent.
 * CustomerD owns the relationship and fully controls the profile lifecycle.
 */
@Entity
@Table(name = "customer_d")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerD {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(
            name = "profile_id",
            unique = true,
            nullable = true
    )
    @Getter
    private ProfileD profile;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerD(String displayName) {
        String normalized = (displayName == null ? null : displayName.strip());
        if (normalized == null || normalized.isEmpty()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = normalized;
    }

    public ProfileD createProfile(boolean marketingOptIn) {
        if (this.profile != null) {
            throw new IllegalStateException("Customer already has a Profile");
        }
        ProfileD profile = new ProfileD(marketingOptIn);
        this.profile = profile;
        return profile;
    }

    public void removeProfile() {
        if (this.profile == null) {
            throw new IllegalStateException("Customer has no Profile to remove");
        }
        this.profile = null;
    }
}