package uk.bit1.spring_jpa.entity_4;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileD {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Long id;

//    @MapsId("id") // maps to primary "id"
//    @OneToOne
//    @JoinColumn(name = "id") // foreign key column in T_PRODUCT Table
    @OneToOne(mappedBy = "profile") // think 'Customer.customer'
    private CustomerD customer;

    @Getter
    private boolean marketingOptIn = false;

    ProfileD(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }

    void setCustomerInternal(CustomerD customer) {
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

