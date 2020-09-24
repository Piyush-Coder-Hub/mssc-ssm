package piyush.springframework.msscssm.services;

import javax.transaction.Transactional;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import piyush.springframework.msscssm.domain.Payment;
import piyush.springframework.msscssm.domain.PaymentEvent;
import piyush.springframework.msscssm.domain.PaymentState;
import piyush.springframework.msscssm.repository.PaymentRepository;

/**
 * 
 * @author Piyush
 *
 */

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

	public static final String PAYMENT_ID_HEADER = "payment_Id";
	private final PaymentRepository paymentRepository;
	private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
	private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

	@Override
	public Payment newPayment(Payment payment) {
		payment.setState(PaymentState.NEW);
		return paymentRepository.save(payment);
	}

	@Transactional
	@Override
	public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
		StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
		sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE);
		return sm;
	}

	@Transactional
	@Override
	public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
		StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
		sendEvent(paymentId, sm, PaymentEvent.AUTH_APPROVED);
		return sm;
	}

	@Transactional
	@Override
	public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
		StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
		sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);
		return sm;
	}

	private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {
		Message msg = MessageBuilder.withPayload(event).setHeader(PAYMENT_ID_HEADER, paymentId).build();

		sm.sendEvent(msg);
	}

	/**
	 * Get state from DataBase & change state of state machine from state in DB &
	 * then start it again
	 * 
	 * @param paymentId
	 * @return
	 */
	private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {

		Payment payment = paymentRepository.getOne(paymentId);

		StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(paymentId));
		sm.stop();

		sm.getStateMachineAccessor().doWithAllRegions(sma -> {
			sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
			sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(), null, null, null, null));

		});

		sm.start();
		return sm;

	}

}
