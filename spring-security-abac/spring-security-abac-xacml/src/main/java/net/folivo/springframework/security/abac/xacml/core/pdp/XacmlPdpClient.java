package net.folivo.springframework.security.abac.xacml.core.pdp;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPException;

import net.folivo.springframework.security.abac.pdp.PdpClient;

public class XacmlPdpClient implements PdpClient<Request, Response> {

	private final PDPEngine engine;

	public XacmlPdpClient(PDPEngine engine) {
		this.engine = engine;
	}

	// TODO should throw exception!
	@Override
	public Response decide(Request request) {
		Response response = null;
		try {
			response = engine.decide(request);
		} catch (PDPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
}
