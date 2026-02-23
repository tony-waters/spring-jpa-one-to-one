package uk.bit1.spring_jpa.scenarioB;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepositoryB extends JpaRepository<CustomerB, Long> {
}
