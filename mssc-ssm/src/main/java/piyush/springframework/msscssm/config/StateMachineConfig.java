package piyush.springframework.msscssm.config;

import static piyush.springframework.msscssm.domain.PaymentEvent.PRE_AUTHORIZE;
import static piyush.springframework.msscssm.domain.PaymentEvent.PRE_AUTH_DECLINED;
import static piyush.springframework.msscssm.domain.PaymentState.NEW;
import static piyush.springframework.msscssm.domain.PaymentState.PRE_AUTH;
import static piyush.springframework.msscssm.domain.PaymentState.PRE_AUTH_ERROR;

import java.util.EnumSet;
import java.util.Random;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import lombok.extern.slf4j.Slf4j;
import piyush.springframework.msscssm.domain.PaymentEvent;
import piyush.springframework.msscssm.domain.PaymentState;
import piyush.springframework.msscssm.services.PaymentServiceImpl;

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
		.action(preAuthAction()).guard(paymentGuard())
		.and()
		.withExternal().source(NEW).target(PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
		.and()
		.withExternal().source(NEW).target(PRE_AUTH_ERROR).event(PRE_AUTH_DECLINED);
	}

	@Override
	public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
		StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {

			@Override
			public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
				log.info(String.format("stateChanged(from:%s,to:%s", from, to));
			}

		};
		config.withConfiguration().listener(adapter);
	}
	
	public Guard<PaymentState, PaymentEvent> paymentGuard(){
		return context ->{
			return context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)!=null;
			
		};
	}
	
	public Action<PaymentState, PaymentEvent> preAuthAction(){
		
		return context -> {
			System.out.println("Pre Auth was Called!!!!");
			if(new Random().nextInt(10)<8) {
				System.out.println("Approved");
				context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
						.setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
						.build());
			}else {
				System.out.println("Declined No Credit!!!!");
				context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
						.setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
						.build());
			}
		};
		
	}

}
