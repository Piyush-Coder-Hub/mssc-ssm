package piyush.springframework.msscssm.config;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import lombok.extern.slf4j.Slf4j;
import piyush.springframework.msscssm.domain.PaymentEvent;
import piyush.springframework.msscssm.domain.PaymentState;

@Slf4j
@SpringBootTest
class StateMachineConfigTest {

	@Autowired
	StateMachineFactory<PaymentState, PaymentEvent> factory;

	@Test
	void testNewStateMachine() {

		StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());
		stateMachine.start();

		log.debug(stateMachine.getState().toString());

		stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);

		log.debug(stateMachine.getState().toString());

		stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);

		log.debug(stateMachine.getState().toString());

	}

}
