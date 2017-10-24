package net.folivo.springframework.security.abac.xacml.core.zold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdResponse;
import com.att.research.xacml.std.StdResult;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONResponse;
import com.att.research.xacml.std.json.JSONStructureException;

//TODO testen
@RestController
public class PDPController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final PDPEngine pdp;

	@Autowired
	public PDPController(PDPEngine pdp) {
		this.pdp = pdp;
	}

	// TODO !ABSICHERN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// entweder per firewall oder authenticiated
	@PostMapping("/api/pdp")
	public String evaluate(@RequestBody String jsonRequest) {
		Response response;
		try {
			response = pdp.decide(JSONRequest.load(jsonRequest));
		} catch (PDPException | JSONStructureException e) {
			log.error("Problem with response.", e);
			response = new StdResponse(new StdResult(Decision.INDETERMINATE,
					new StdStatus(new StdStatusCode(new IdentifierImpl("NULL")))));
		}

		try {
			return JSONResponse.toString(response);
		} catch (Exception e) {
			log.error("Problem with parsing response to json.", e);
			return "";
		}
	}
}
