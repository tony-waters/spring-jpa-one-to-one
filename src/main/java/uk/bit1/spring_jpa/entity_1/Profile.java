package uk.bit1.spring_jpa.entity_1;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    @Getter
    private Long id;

    @OneToOne(mappedBy = "profile")
    private Customer customer;

    @Getter
    private boolean marketingOptIn = false;

    Profile(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }

//    void setCustomerInternal(Customer customer) {
//        this.customer = customer;
//    }

    void setCustomerInternal(Customer customer) {
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

