package uk.bit1.spring_jpa.scenarioE;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PROFILE_E")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileE {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    @Column(nullable = false)
    private boolean marketingOptIn = false;

    ProfileE(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }

}

