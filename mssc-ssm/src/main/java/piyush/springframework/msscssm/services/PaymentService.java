package piyush.springframework.msscssm.services;

import org.springframework.statemachine.StateMachine;

import piyush.springframework.msscssm.domain.Payment;
import piyush.springframework.msscssm.domain.PaymentEvent;
import piyush.springframework.msscssm.domain.PaymentState;

/**
 * 
 * @author Piyush
 *
 */
public interface PaymentService {

	Payment newPayment(Payment payment);

	public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

	public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);

	public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);
}
