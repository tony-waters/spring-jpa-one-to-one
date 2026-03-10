package uk.bit1.spring_jpa.variantE;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_e")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerE {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerE(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = displayName.strip();
    }

    // no lifecycle management here
    // relationship is managed from the service layer
}