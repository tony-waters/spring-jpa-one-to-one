package uk.bit1.spring_jpa.variantC;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_C")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerC {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    // Inverse side
    @Getter(AccessLevel.PUBLIC)
    @OneToOne(
            mappedBy = "customer", // think 'Profile.customer'
            fetch =  FetchType.LAZY, // this will not always work
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ProfileC profile;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerC(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    // Parent side lifecycle control
    public ProfileC createProfile(boolean marketingOptIn) {
        if (this.profile != null) {
            throw new IllegalStateException("Customer already has a Profile");
        }
        ProfileC profile = new ProfileC(marketingOptIn);
        profile.setCustomerInternal(this); // owning side first
        this.profile = profile;            // inverse side second
        return profile;
    }

    // Parent side lifecycle control
    public void removeProfile() {
        if (this.profile == null) {
            throw new IllegalStateException("Customer has no Profile to remove");
        }
        ProfileC oldProfile = this.profile;
        this.profile = null;
        oldProfile.clearCustomerInternal();
    }

}
