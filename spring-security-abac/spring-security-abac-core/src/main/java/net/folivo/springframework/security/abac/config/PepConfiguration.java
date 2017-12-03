package net.folivo.springframework.security.abac.config;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

import net.folivo.springframework.security.abac.pep.PepClient;
import net.folivo.springframework.security.abac.pep.RequestAttributePostProcessor;
import net.folivo.springframework.security.abac.pep.SimplePepClient;

@Configuration
@EnableGlobalAuthentication
public abstract class PepConfiguration<T> {

	protected final PdpConfiguration<?, ?> pdpConfig;
	protected AuthenticationManager authManager;
	protected AuthenticationManagerBuilder authBuilder;
	private final AuthenticationConfiguration authConfig;
	protected boolean disableAuthenticationRegistry;
	private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
		@Override
		public <O> O postProcess(O object) {
			throw new IllegalStateException(ObjectPostProcessor.class.getName() + " is a required bean.");
		}
	};

	public PepConfiguration(AuthenticationConfiguration authConfig, PdpConfiguration<?, ?> pdpConfig) {
		this.pdpConfig = pdpConfig;
		this.authConfig = authConfig;
	}

	public PepClient<T> pepClient() {
		return new SimplePepClient<>(pdpConfig.pepEngine(), requestAttributePostProcessors());
	}

	/**
	 * Gets the {@link AuthenticationManager} to use. The default strategy is if
	 * {@link #configure(AuthenticationManagerBuilder)} method is overridden to use
	 * the {@link AuthenticationManagerBuilder} that was passed in. Otherwise,
	 * autowire the {@link AuthenticationManager} by type.
	 *
	 * @return
	 * @throws Exception
	 */
	protected AuthenticationManager authenticationManager() throws Exception {
		if (authManager != null) {

			// TODO research why we must create an eventPublisher
			DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor
					.postProcess(new DefaultAuthenticationEventPublisher());
			authBuilder = new AuthenticationManagerBuilder(objectPostProcessor);
			authBuilder.authenticationEventPublisher(eventPublisher);

			configure(authBuilder);
			if (disableAuthenticationRegistry) {
				authManager = authConfig.getAuthenticationManager();
			} else {
				authManager = authBuilder.build();
			}
		}
		return authManager;
	}

	/**
	 * Sub classes can override this method to register different types of
	 * authentication. If not overridden,
	 * {@link #configure(AuthenticationManagerBuilder)} will attempt to autowire by
	 * type.
	 *
	 * @param auth
	 *            the {@link AuthenticationManagerBuilder} used to register
	 *            different authentication mechanisms for the global method
	 *            security.
	 * @throws Exception
	 */
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		this.disableAuthenticationRegistry = true;
	}

	@Autowired
	public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
		this.objectPostProcessor = objectPostProcessor;
	}

	protected abstract Collection<RequestAttributePostProcessor<T>> requestAttributePostProcessors();

}
