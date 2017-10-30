package net.folivo.springframework.security.abac.xacml.core.pdp;

import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPException;

import net.folivo.springframework.security.abac.pdp.PdpClient;
import net.folivo.springframework.security.abac.pdp.RequestHolder;
import net.folivo.springframework.security.abac.pdp.ResponseHolder;

public class XacmlPdpClient implements PdpClient {

	private final PDPEngine engine;

	public XacmlPdpClient(PDPEngine engine) {
		this.engine = engine;
	}

	// TODO should throw exception!
	@Override
	public ResponseHolder decide(RequestHolder requestHolder) {
		// TODO I hate casting but what is the better solution?!
		XacmlRequestHolder realRequestHolder = (XacmlRequestHolder) requestHolder;
		Response response = null;
		try {
			response = engine.decide(realRequestHolder.getRequest());
		} catch (PDPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResponseHolder responseHolder = new XacmlResponseHolder(response);
		return responseHolder;
	}

}
