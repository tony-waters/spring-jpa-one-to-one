package uk.bit1.spring_jpa.entity_1;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    // Customer is the Owning side
    // Customer is the Parent side
    // relationship is Unidirectional
    @Getter
    @OneToOne()
    @JoinColumn(name = "profile") // Owning side is here
    private Profile profile;

    @Getter
    private String displayName;

    public Customer(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    public Profile createProfile(boolean marketingOptIn) {
        this.profile = new Profile(marketingOptIn);
        profile.setCustomerInternal(this);
        return profile;
    }

    public void removeProfile() {
        this.profile = null;
    }

}
