package uk.bit1.spring_jpa.scenarioA;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepositoryA extends JpaRepository<CustomerA, Long> {
}
