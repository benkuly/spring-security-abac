package net.folivo.springframework.security.abac.xacml.core.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.ow2.authzforce.core.pdp.api.CloseableDesignatedAttributeProvider.DependencyAwareFactory;
import org.ow2.authzforce.core.pdp.api.DecisionCache;
import org.ow2.authzforce.core.pdp.api.PdpEngine;
import org.ow2.authzforce.core.pdp.api.XmlUtils.XmlnsFilteringParserFactory;
import org.ow2.authzforce.core.pdp.api.combining.CombiningAlgRegistry;
import org.ow2.authzforce.core.pdp.api.expression.ExpressionFactory;
import org.ow2.authzforce.core.pdp.api.io.XacmlJaxbParsingUtils;
import org.ow2.authzforce.core.pdp.api.policy.RootPolicyProvider;
import org.ow2.authzforce.core.pdp.api.policy.StaticRefPolicyProvider;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactory;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactoryRegistry;
import org.ow2.authzforce.core.pdp.api.value.IntegerValue;
import org.ow2.authzforce.core.pdp.api.value.SimpleValue.StringParseableValueFactory;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.core.pdp.impl.combining.StandardCombiningAlgorithm;
import org.ow2.authzforce.core.pdp.impl.expression.DepthLimitingExpressionFactory;
import org.ow2.authzforce.core.pdp.impl.func.FunctionRegistry;
import org.ow2.authzforce.core.pdp.impl.func.StandardFunction;
import org.ow2.authzforce.core.pdp.impl.policy.CoreRootPolicyProvider;
import org.ow2.authzforce.core.pdp.impl.value.StandardAttributeValueFactories;
import org.ow2.authzforce.core.xmlns.pdp.StandardEnvironmentAttributeSource;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;

import net.folivo.springframework.security.abac.attributes.RequestAttributeFactory;
import net.folivo.springframework.security.abac.attributes.RequestAttributeProvider;
import net.folivo.springframework.security.abac.attributes.StandardProviderCollector;
import net.folivo.springframework.security.abac.context.RequestContextMapper;
import net.folivo.springframework.security.abac.context.SimpleRequestContextMapper;
import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pip.ContextMappingPipEngine;
import net.folivo.springframework.security.abac.pip.PipEngine;
import net.folivo.springframework.security.abac.pip.PipProviderContext;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlPdpClient;

//TOTO isn't there a better solution without T?
public class XacmlPdpConfiguration<T> {

	// TODO I do things here, that are from PdpEngineConfiguration
	@Bean
	public PdpEngine pdpEngine() {

		boolean enableXPath = false;
		AttributeValueFactoryRegistry attValFactoryRegistry = StandardAttributeValueFactories.getRegistry(enableXPath,
				Optional.empty());
		// TODO I thing that should be issued in AuthZForce
		final AttributeValueFactory<?> intValFactory = attValFactoryRegistry
				.getExtension(StandardDatatypes.INTEGER.getId());
		assert intValFactory != null && intValFactory.getDatatype() == StandardDatatypes.INTEGER
				&& intValFactory instanceof StringParseableValueFactory;
		@SuppressWarnings("unchecked")
		FunctionRegistry functionRegistry = StandardFunction.getRegistry(enableXPath,
				(StringParseableValueFactory<IntegerValue>) intValFactory);
		int maxVarRefDepth = -1;
		boolean strictAttributeIssuerMatch = false;

		// TODO
		List<DependencyAwareFactory> attProviderFactories = new ArrayList<>();

		/*
		 * XACML Expression factory/parser
		 */
		ExpressionFactory xacmlExpressionFactory;
		try {
			xacmlExpressionFactory = new DepthLimitingExpressionFactory(attValFactoryRegistry, functionRegistry,
					attProviderFactories, maxVarRefDepth, enableXPath, strictAttributeIssuerMatch);
		} catch (IllegalArgumentException | IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			throw new BeanCreationException("Couldn't create PdpEngine", e2);
		}

		XmlnsFilteringParserFactory xacmlParserFactory = XacmlJaxbParsingUtils.getXacmlParserFactory(enableXPath);
		CombiningAlgRegistry combiningAlgRegistry = StandardCombiningAlgorithm.REGISTRY;
		Optional<StaticRefPolicyProvider> refPolicyProvider = Optional.empty();

		RootPolicyProvider rootPolicyProvider;
		try {
			URL url = ResourceUtils.getURL("src/main/resources/xacml/policy.xml");
			rootPolicyProvider = CoreRootPolicyProvider.getInstance(url, xacmlParserFactory, xacmlExpressionFactory,
					combiningAlgRegistry, refPolicyProvider);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				xacmlExpressionFactory.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BeanCreationException("Couldn't create PdpEngine", e);
		}

		StandardEnvironmentAttributeSource stdEnvAttributeSource = StandardEnvironmentAttributeSource.REQUEST_ELSE_PDP;
		// TODO performance
		Optional<DecisionCache> decisionCache = Optional.empty();
		try {
			return new BasePdpEngine(xacmlExpressionFactory, rootPolicyProvider, strictAttributeIssuerMatch,
					stdEnvAttributeSource, decisionCache);
		} catch (IllegalArgumentException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new BeanCreationException("Couldn't create PdpEngine", e1);
		}
	}

	@Bean
	protected RequestContextMapper<T> requestContextMapper() {
		return new SimpleRequestContextMapper<>();
	}

	@Bean
	protected PipEngine<T> pipEngine() {
		// TODO
		List<RequestAttributeProvider<PipProviderContext<T>>> providers = Collections.emptyList();
		RequestAttributeProvider<PipProviderContext<T>> collector = new StandardProviderCollector<>(providers);
		return new ContextMappingPipEngine<>(collector, requestContextMapper());
	}

	@Bean
	protected PdpClient<T> pdpClient(RequestAttributeFactory attributeFactory) {
		return new XacmlPdpClient<>(pdpEngine(), requestContextMapper(), attributeFactory);
	}
}
