package uk.bit1.spring_jpa.scenarioE;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerE {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    @MapsId("id") // maps to primary "id"
    @OneToOne
    @JoinColumn(name = "id") // foreign key column in T_PRODUCT Table
    private ProfileE profile;

    @Getter
    private String displayName;

    public CustomerE(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    public ProfileE createProfile(boolean marketingOptIn) {
        this.profile = new ProfileE(marketingOptIn);
//        profile.setCustomerInternal(this);
        return profile;
    }

    public void removeProfile() {
        this.profile = null;
    }

}
