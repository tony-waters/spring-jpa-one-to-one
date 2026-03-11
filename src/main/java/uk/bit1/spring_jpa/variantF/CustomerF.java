package uk.bit1.spring_jpa.variantF;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_F")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerF {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerF(String displayName) {
        String normalised = null;
        if(displayName != null) {
            normalised = displayName.strip();
        }
        if (normalised == null || normalised.isEmpty()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = normalised;
    }

    // No reference to Profile
    // Lifecycle ordering is handled by the caller.
    // (this would be pushed to the Service layer)

}
