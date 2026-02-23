package uk.bit1.spring_jpa.scenarioA;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerA {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    // Customer is the Owning side - “FK in Customer table” (customer.profile_id)
    // Customer is the Parent side
    // relationship is Bidirectional
    @Getter
    @OneToOne(
            cascade = CascadeType.ALL,  // 'cascade' is on the Parent Side
            orphanRemoval = true        // 'orphanRemoval' is on the Parent Side
    )
    @JoinColumn( // '@JoinColumn / @JoinTable' is on the Owning Side
            name = "profile_id",
            unique = true   // Without unique=true we have actually built “many customers can
                            // point to the same profile” (the object model says one-to-one,
                            // but the DB allows many-to-one).
    )
    private ProfileA profile;

    @Getter
    private String displayName;

    public CustomerA(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    public ProfileA createProfile(boolean marketingOptIn) {
        this.profile = new ProfileA(marketingOptIn);
        profile.setCustomerInternal(this);
        return profile;
    }

    public void removeProfile() {
        this.profile = null;
    }

}
