package uk.bit1.spring_jpa.variantF;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Variant F: unidirectional one-to-one with shared primary key via @MapsId.
 * CustomerF has no reference to ProfileF; lifecycle ordering is handled by the caller.
 */
@Entity
@Table(name = "customer_f")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerF {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    @Column(nullable = false, length = 80)
    private String displayName;

    public CustomerF(String displayName) {
        String normalized = (displayName == null ? null : displayName.strip());
        if (normalized == null || normalized.isEmpty()) {
            throw new IllegalArgumentException("displayName must have a value");
        }
        this.displayName = normalized;
    }
}