package net.folivo.springframework.security.abac.pdp;

public interface PdpClient {

	Response decide(Request request);

}
