package net.folivo.springframework.security.abac.contexthandler;

public interface PdpClient<R, S> {

	S decide(R request);

}
