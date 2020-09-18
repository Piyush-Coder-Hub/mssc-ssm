package piyush.springframework.msscssm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import piyush.springframework.msscssm.domain.Payment;

/**
 * 
 * @author Piyush
 *
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
