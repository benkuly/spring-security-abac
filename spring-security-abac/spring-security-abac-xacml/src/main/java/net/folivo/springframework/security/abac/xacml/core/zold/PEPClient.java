package net.folivo.springframework.security.abac.xacml.core.zold;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.Result;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdMutableRequest;
import com.att.research.xacml.std.StdResponse;
import com.att.research.xacml.std.StdResult;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONResponse;

import net.folivo.spring.xacml.core.ResourceAttributeProvider;
import net.folivo.spring.xacml.core.StaticAttributeProvider;

//TODO testen
/**
 * PEP
 * 
 * @author Benedict Benken
 *
 */
@Component
public class PEPClient {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final PDPClient pdpClient;

	private final List<StaticAttributeProvider> staticAttributeProviders;
	private final List<ResourceAttributeProvider> resourceAttributeProviders;

	@Autowired(required = false)
	public PEPClient(PDPClient pdpClient, List<StaticAttributeProvider> staticAttributeProviders,
			List<ResourceAttributeProvider> resourceAttributeProviders) {
		if (pdpClient == null)
			throw new IllegalArgumentException("pdpClient must not be null");
		this.pdpClient = pdpClient;
		this.staticAttributeProviders = staticAttributeProviders;
		this.resourceAttributeProviders = resourceAttributeProviders;
	}

	/**
	 * Checks the permission to a specific object.
	 * 
	 * @param authentication
	 * @param resource
	 * @param action
	 * @throws AccessDeniedException
	 */
	public void checkPermission(Authentication authentication, Object resource, String action)
			throws AccessDeniedException {
		if (!hasPermission(authentication, resource, action))
			throw new AccessDeniedException("Access is denied");
	}

	/**
	 * Checks the permission to a specific object. Uses @{Authentication}
	 * from @{SecurityContextHolder}.
	 * 
	 * @param resource
	 * @param action
	 * @throws AccessDeniedException
	 */
	public void checkPermission(Object resource, String action) throws AccessDeniedException {
		checkPermission(SecurityContextHolder.getContext().getAuthentication(), resource, action);
	}

	/**
	 * Get the permission to a specific object.
	 * 
	 * @param authentication
	 * @param resource
	 * @param action
	 * @return True if having the permission.
	 */
	public boolean hasPermission(Authentication authentication, Object resource, String action) {
		Response response = request(authentication, resource, action);

		for (Result result : response.getResults()) {
			if (result.getDecision() == Decision.PERMIT) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the permission to a specific object. Uses @{Authentication}
	 * from @{SecurityContextHolder}.
	 * 
	 * @param resource
	 * @param action
	 * @return True if having the permission.
	 * @throws AccessDeniedException
	 */
	public boolean hasPermission(Object resource, String action) throws AccessDeniedException {
		return hasPermission(SecurityContextHolder.getContext().getAuthentication(), resource, action);
	}

	/**
	 * Get the response to a specific object-permission.
	 * 
	 * @param authentication
	 * @param resource
	 * @param action
	 * @return The response
	 */
	public Response request(Authentication authentication, Object resource, String action) {
		if (authentication != null && resource != null && action != null) {
			StdMutableRequest request = new StdMutableRequest();
			for (StaticAttributeProvider provider : staticAttributeProviders) {
				request.add(provider.getAttributes(authentication, resource, action));
			}
			// TODO
			for (ResourceAttributeProvider provider : resourceAttributeProviders) {
				if (provider.supports(resource.getClass())) {
					request.add(provider.getAttributes(authentication, resource, action));
				}
			}
			return request(request);
		}
		return new StdResponse(
				new StdResult(Decision.INDETERMINATE, new StdStatus(new StdStatusCode(new IdentifierImpl("NULL")))));
	}

	/**
	 * Get the response to a specific object-permission.
	 * 
	 * @param resource
	 * @param action
	 * @return The response
	 */
	public Response request(Object resource, String action) {
		return request(SecurityContextHolder.getContext().getAuthentication(), resource, action);
	}

	/**
	 * Request the XACML-PDP manually.
	 * 
	 * @param request
	 * @return
	 */
	public Response request(Request request) {
		Response response;
		try {
			String jsonRequest = JSONRequest.toString(request);
			log.debug(jsonRequest);
			String jsonResponse = pdpClient.request(jsonRequest);
			response = JSONResponse.load(jsonResponse);
		} catch (Exception e) {
			log.error("Problem with doing request.", e);
			response = new StdResponse(new StdResult(Decision.INDETERMINATE,
					new StdStatus(new StdStatusCode(new IdentifierImpl("NULL")))));
		}
		// TODO weg damit
		for (Result r : response.getResults()) {
			log.debug(r.getDecision() + " " + r.getStatus().getStatusMessage());
		}
		return response;
	}
}
