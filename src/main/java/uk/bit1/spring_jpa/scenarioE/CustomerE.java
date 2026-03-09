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

//    @Getter
//    // Inverse Side
//    @OneToOne(
//            mappedBy = "customer", // think 'profile.customer'
//            cascade = CascadeType.ALL, // 'cascade' is on the Parent Side
//            orphanRemoval = true // 'orphanRemoval' is on the Parent side
//    )
//    private ProfileB profile;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerE(String displayName) {
        if(displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    // no lifecycle management for Profile
    // control is done from Service instead

}
