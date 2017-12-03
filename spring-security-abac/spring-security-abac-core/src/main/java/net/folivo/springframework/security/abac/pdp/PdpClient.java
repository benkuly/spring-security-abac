package net.folivo.springframework.security.abac.pdp;

public interface PdpClient<R, S> {

	S decide(R request);

}
