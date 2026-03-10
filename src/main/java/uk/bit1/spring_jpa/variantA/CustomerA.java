package uk.bit1.spring_jpa.variantA;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_A")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerA {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    // Owning side
    @Getter
    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,  // 'cascade' is on the Parent Side
            orphanRemoval = true        // 'orphanRemoval' is on the Parent Side
    )
    @JoinColumn( // '@JoinColumn / @JoinTable' is on the Owning Side
            name = "profile_id",
            unique = true   // 'unique' enforces 1-1 in DB
    )
    private ProfileA profile;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerA(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    // Parent side - lifecycle control lives here
    public ProfileA createProfile(boolean marketingOptIn) {
        if (this.profile != null) {
            throw new IllegalStateException("Customer already has a Profile");
        }
        this.profile = new ProfileA(marketingOptIn);
        profile.setCustomerInternal(this);
        return profile;
    }

    // Parent side - lifecycle control lives here
    public void removeProfile() {
        if (this.profile == null) {
            throw new IllegalStateException("Customer has no Profile to remove");
        }
        ProfileA old =  this.profile;
        this.profile = null;
        old.clearCustomerInternal();
    }

}
