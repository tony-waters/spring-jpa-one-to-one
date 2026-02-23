package uk.bit1.spring_jpa.scenarioB;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerB {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;


    @Getter
    @OneToOne(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    ) // think 'Profile.customer'
    private ProfileB profile;

    @Getter
    private String displayName;

    public CustomerB(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    public ProfileB createProfile(boolean marketingOptIn) {
        this.profile = new ProfileB(marketingOptIn);
        profile.setCustomerInternal(this);
        return profile;
    }

    public void removeProfile() {
        this.profile = null;
    }

}
