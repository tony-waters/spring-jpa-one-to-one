package uk.bit1.spring_jpa.entity_5;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileE {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    private boolean marketingOptIn = false;

    ProfileE(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }

//    void setCustomerInternal(CustomerE customer) {
//        if (customer == null) {
//            throw new IllegalArgumentException("Profile must have a Customer");
//        }
//        if (this.customer != null && !this.customer.equals(customer)) {
//            throw new IllegalStateException("Profile cannot be moved to another Customer");
//        }
//        this.customer = customer;
//    }

//    void clearCustomerInternal() {
//        this.customer = null;
//    }

}

