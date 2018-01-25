package net.folivo.springframework.security.abac.xacml.core.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ow2.authzforce.core.pdp.api.CloseableDesignatedAttributeProvider.DependencyAwareFactory;
import org.ow2.authzforce.core.pdp.api.DecisionCache;
import org.ow2.authzforce.core.pdp.api.DecisionRequest;
import org.ow2.authzforce.core.pdp.api.DecisionResult;
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
import net.folivo.springframework.security.abac.attributes.SimpleRequestAttributeFactory;
import net.folivo.springframework.security.abac.config.PdpConfiguration;
import net.folivo.springframework.security.abac.contexthandler.RequestContextHandler;
import net.folivo.springframework.security.abac.contexthandler.SimpleRequestContextHandler;
import net.folivo.springframework.security.abac.method.MethodInvocationContext;
import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlPdpClient;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlRequestFactory;
import net.folivo.springframework.security.abac.xacml.core.pdp.XacmlResponseEvaluator;

public class XacmlPdpConfiguration
		implements PdpConfiguration<DecisionRequest, DecisionResult, MethodInvocationContext> {

	// I do things here, that are from PdpEngineConfiguration
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
			rootPolicyProvider = CoreRootPolicyProvider.getInstance(
					ResourceUtils.getURL("src/main/resources/xacml/policy.xml"), xacmlParserFactory,
					xacmlExpressionFactory, combiningAlgRegistry, refPolicyProvider);
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

	@Override
	public RequestFactory<DecisionRequest> requestFactory() {
		return new XacmlRequestFactory(pdpEngine());
	}

	@Override
	public PdpClient<DecisionRequest, DecisionResult> pdpClient() {
		return new XacmlPdpClient(pdpEngine());
	}

	@Override
	public ResponseEvaluator<DecisionResult> responseEvaluator() {
		return new XacmlResponseEvaluator();
	}

	@Override
	public RequestAttributeFactory requestAttributeFactory() {
		return new SimpleRequestAttributeFactory();
	}

	@Override
	public RequestContextHandler<MethodInvocationContext> requestContextHandler() {
		return new SimpleRequestContextHandler<>(pdpClient(),
				responseEvaluator(), requestFactory());
	}
}
