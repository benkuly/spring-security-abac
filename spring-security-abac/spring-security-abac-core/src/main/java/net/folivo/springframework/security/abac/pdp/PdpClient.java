package net.folivo.springframework.security.abac.pdp;

public interface PdpClient {

	PdpResponse decide(PdpRequest request);

}
