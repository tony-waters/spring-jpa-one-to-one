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
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    // Unidirectional - Profile does NOT know about Customer
    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(
            name = "profile_id",
            unique = true
    )
    @Getter // since Profile does not reference Customer no reason to not provide a public getter
    private ProfileD profile;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerD(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    // Parent side - lifecycle control lives here
    public ProfileD createProfile(boolean marketingOptIn) {
        if (this.profile != null) {
            throw new IllegalStateException("Customer already has a Profile");
        }
        this.profile = new ProfileD(marketingOptIn);
        return profile;
    }

    // Parent side - lifecycle control lives here
    public void removeProfile() {
        if (this.profile == null) {
            throw new IllegalStateException("Customer has no Profile to remove");
        }
        this.profile = null;
    }

}
