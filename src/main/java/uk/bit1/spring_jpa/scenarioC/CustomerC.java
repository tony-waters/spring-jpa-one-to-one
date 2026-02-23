package uk.bit1.spring_jpa.scenarioC;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerC {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    // Customer is the Inverse side
    // Customer is the Parent side
    // relationship is Bidirectional
    @Getter
    @OneToOne(
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

    public ProfileC createProfile(boolean marketingOptIn) {
        this.profile = new ProfileC(marketingOptIn);
        profile.setCustomerInternal(this);
        return profile;
    }

    public void removeProfile() {
        this.profile = null;
    }

}
