package uk.bit1.spring_jpa.scenarioC;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileC {

    @Id
//    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "customer_id") //
    private CustomerC customer;

    @Getter
    private boolean marketingOptIn = false;

    ProfileC(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }

    void setCustomerInternal(CustomerC customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Profile must have a Customer");
        }
        if (this.customer != null && !this.customer.equals(customer)) {
            throw new IllegalStateException("Profile cannot be moved to another Customer");
        }
        this.customer = customer;
    }

    void clearCustomerInternal() {
        this.customer = null;
    }

}

