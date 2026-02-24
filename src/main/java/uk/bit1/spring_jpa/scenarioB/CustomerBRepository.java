package uk.bit1.spring_jpa.scenarioB;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerBRepository extends JpaRepository<CustomerB, Long> {
}
