package uk.bit1.spring_jpa.variantD;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Variant D: unidirectional one-to-one with foreign key in parent.
 * ProfileD has no reference back to CustomerD.
 */
@Entity
@Table(name = "profile_d")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileD {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    @Column(nullable = false)
    private boolean marketingOptIn = false;

    ProfileD(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }
}