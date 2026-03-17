package uk.bit1.spring_jpa.variantD;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_D")
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
        String normalized = displayName == null ? null : displayName.strip();
        if (normalized == null || normalized.isEmpty()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = normalized;
    }

    public ProfileD createProfile(boolean marketingOptIn) {
        if (this.profile != null) {
            throw new IllegalStateException("Customer already has a Profile");
        }
        this.profile = new ProfileD(marketingOptIn);
        return profile;
    }

    public void attachProfile(ProfileD profile) {
        if (profile == null) {
            throw new IllegalArgumentException("profile must not be null");
        }
        if (this.profile != null) {
            throw new IllegalStateException("Customer already has a Profile");
        }
        this.profile = profile;
    }

    public void removeProfile() {
        if (this.profile == null) {
            throw new IllegalStateException("Customer has no Profile to remove");
        }
        this.profile = null;
    }
}