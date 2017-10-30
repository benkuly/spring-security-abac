package net.folivo.springframework.security.abac.xacml.core.pdp;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.pdp.PDPEngine;

import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.PdpRequest;
import net.folivo.springframework.security.abac.pdp.PdpResponse;

public class XacmlPdpClient implements PdpClient {

	private final PDPEngine engine;

	public XacmlPdpClient(PDPEngine engine) {
		this.engine = engine;
	}

	@Override
	public PdpResponse decide(PdpRequest request) {
		return engine.decide((Request) request);
	}

}
