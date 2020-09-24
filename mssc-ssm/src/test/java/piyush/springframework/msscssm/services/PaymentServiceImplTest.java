package piyush.springframework.msscssm.services;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import piyush.springframework.msscssm.domain.Payment;
import piyush.springframework.msscssm.domain.PaymentEvent;
import piyush.springframework.msscssm.domain.PaymentState;
import piyush.springframework.msscssm.repository.PaymentRepository;

@SpringBootTest
class PaymentServiceImplTest {

	@Autowired
	PaymentService paymentService;

	@Autowired
	PaymentRepository paymentRepository;

	Payment payment;

	@BeforeEach
	void setUp() {
		payment = Payment.builder().amount(new BigDecimal(12.99)).build();
	}

	@Transactional
	//@Test
	@RepeatedTest(5)
	void preAuth() {
		Payment savedPayment = paymentService.newPayment(payment);
		System.out.println(savedPayment.getState());
		StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());

		Payment preAuthenticatedPayment = paymentRepository.getOne(savedPayment.getId());
		System.out.println(sm.getState().getId());
		System.out.println(preAuthenticatedPayment);
	}

}
