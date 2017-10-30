package net.folivo.springframework.security.abac.xacml.core.pdp;

import com.att.research.xacml.api.Response;
import com.att.research.xacml.std.StdMutableResponse;

import net.folivo.springframework.security.abac.pdp.PdpResponse;

public class XacmlResponse extends StdMutableResponse implements Response, PdpResponse {

}
