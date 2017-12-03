package net.folivo.springframework.security.abac.config;

import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.RequestAttributeFactory;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;
import net.folivo.springframework.security.abac.pep.PepEngine;

public interface PdpConfiguration<R, S> {

	PdpClient<R, S> pdpClient();

	RequestFactory<R> requestFactory();

	ResponseEvaluator<S> responseEvaluator();

	RequestAttributeFactory requestAttributeFactory();

	PepEngine pepEngine();

}
