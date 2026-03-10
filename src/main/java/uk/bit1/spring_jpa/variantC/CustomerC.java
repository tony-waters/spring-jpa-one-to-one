package uk.bit1.spring_jpa.variantC;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_c")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerC {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    // Inverse side
    @Getter(AccessLevel.PACKAGE)
    @OneToOne(
            fetch =  FetchType.LAZY, // Doesnt make a difference here
            mappedBy = "customer", // think 'Profile.customer'
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ProfileC profile;

    @Getter
    private String displayName;

    public CustomerC(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    // Parent side - lifecycle control lives here
    public ProfileC createProfile(boolean marketingOptIn) {
        if (this.profile != null) throw new IllegalStateException("Customer already has a Profile");
        this.profile = new ProfileC(marketingOptIn);
        profile.setCustomerInternal(this);
        return profile;
    }

    // Parent side - lifecycle control lives here
    public void removeProfile() {
        if (this.profile == null) throw new IllegalStateException("Customer has no Profile to remove");
        ProfileC old = this.profile;
        this.profile = null;
        old.clearCustomerInternal();
    }

}
