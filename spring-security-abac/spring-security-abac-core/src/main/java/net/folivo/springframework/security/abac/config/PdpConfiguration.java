package net.folivo.springframework.security.abac.config;

import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.RequestFactory;
import net.folivo.springframework.security.abac.pdp.ResponseEvaluator;

public interface PdpConfiguration {

	RequestFactory getRequestFactory();

	PdpClient getPdpClient();

	ResponseEvaluator getResponseEvaluator();

}
