package uk.bit1.spring_jpa.variantE;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PROFILE_E")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileE {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    // Owning side: FK lives in profile_e.customer_id
    @OneToOne(optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false,
            unique = true
    )
    @Getter(AccessLevel.PROTECTED)
    private CustomerE customer;

    @Getter
    @Column(nullable = false)
    private boolean marketingOptIn = false;

    ProfileE(CustomerE customer, boolean marketingOptIn) {
        if (customer == null) {
            throw new IllegalArgumentException("customer must not be null");
        }
        this.customer = customer;
        this.marketingOptIn = marketingOptIn;
    }

    // no lifecycle management here
    // relationship is managed from the service layer
}