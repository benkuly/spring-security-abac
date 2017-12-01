package net.folivo.springframework.security.abac.xacml.core.pdp;

import com.att.research.xacml.api.Response;

import net.folivo.springframework.security.abac.pdp.PdpResponse;

public class XacmlResponseHolder extends ResponseHolderImpl<Response> {

	public XacmlResponseHolder(Response response) {
		super(response);
	}

}
