package net.folivo.springframework.security.abac.contexthandler;

//TODO maybe T is too much, use object?
public interface PdpClient<R, S, T> {

	S decide(R request, T context);

}
