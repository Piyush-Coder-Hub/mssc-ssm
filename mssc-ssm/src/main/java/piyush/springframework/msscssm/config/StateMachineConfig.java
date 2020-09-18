package piyush.springframework.msscssm.config;

import static piyush.springframework.msscssm.domain.PaymentEvent.PRE_AUTHORIZE;
import static piyush.springframework.msscssm.domain.PaymentEvent.PRE_AUTH_DECLINED;
import static piyush.springframework.msscssm.domain.PaymentState.NEW;
import static piyush.springframework.msscssm.domain.PaymentState.PRE_AUTH;
import static piyush.springframework.msscssm.domain.PaymentState.PRE_AUTH_ERROR;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import lombok.extern.slf4j.Slf4j;
import piyush.springframework.msscssm.domain.PaymentEvent;
import piyush.springframework.msscssm.domain.PaymentState;

/**
 * 
 * @author Piyush
 *
 */
@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
	@Override
	public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
		states.withStates().initial(PaymentState.NEW).states(EnumSet.allOf(PaymentState.class)).end(PaymentState.AUTH)
				.end(PaymentState.PRE_AUTH_ERROR).end(PaymentState.AUTH_ERROR);
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
		transitions.withExternal().source(NEW).target(NEW).event(PRE_AUTHORIZE)
		.and()
		.withExternal().source(NEW).target(PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
		.and()
		.withExternal().source(NEW).target(PRE_AUTH_ERROR).event(PRE_AUTH_DECLINED);
	}

}
