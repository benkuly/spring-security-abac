package net.folivo.springframework.security.abac.xacml.core.zold;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

//TODO testen bzw schöner?
@FeignClient("hapalops-pdp")
public interface PDPClient {

	@PostMapping("/api/pdp")
	public String request(String request);
}