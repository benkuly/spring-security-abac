package net.folivo.springframework.security.abac.xacml.core.pdp;

import com.att.research.xacml.api.Request;

import net.folivo.springframework.security.abac.pdp.RequestHolderImpl;

public class XacmlRequestHolder extends RequestHolderImpl<Request> {

	public XacmlRequestHolder(Request request) {
		super(request);
	}

}
