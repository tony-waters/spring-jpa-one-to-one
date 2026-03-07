package uk.bit1.spring_jpa.scenarioD;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_c")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerD {

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
    private ProfileD profile;

    @Getter
    private String displayName;

    public CustomerD(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    // Parent side - lifecycle control lives here
    public ProfileD createProfile(boolean marketingOptIn) {
        if (this.profile != null) throw new IllegalStateException("Customer already has a Profile");
        this.profile = new ProfileD(marketingOptIn);
        profile.setCustomerInternal(this);
        return profile;
    }

    // Parent side - lifecycle control lives here
    public void removeProfile() {
        if (this.profile == null) throw new IllegalStateException("Customer has no Profile to remove");
        ProfileD old = this.profile;
        this.profile = null;
        old.clearCustomerInternal();
    }

}
