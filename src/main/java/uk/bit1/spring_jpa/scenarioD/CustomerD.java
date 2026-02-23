package uk.bit1.spring_jpa.scenarioD;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerD {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    // Customer is the Owning side
    // Customer is the Parent side
    // relationship is Bidirectional
    @Getter
    @MapsId("id") // maps to primary "id"
    @OneToOne
    @JoinColumn(name = "id") // foreign key column in T_PRODUCT Table
    private ProfileD profile;

    @Getter
    private String displayName;

    public CustomerD(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    public ProfileD createProfile(boolean marketingOptIn) {
        this.profile = new ProfileD(marketingOptIn);
        profile.setCustomerInternal(this);
        return profile;
    }

    public void removeProfile() {
        this.profile = null;
    }

}
