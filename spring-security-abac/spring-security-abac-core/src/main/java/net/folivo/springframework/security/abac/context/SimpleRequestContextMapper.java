package net.folivo.springframework.security.abac.context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//TODO only a prototype
public class SimpleRequestContextMapper<T> implements RequestContextMapper<T> {

	private final Map<String, T> contextMap = new HashMap<>();

	@Override
	public String map(T context) {
		String id = UUID.randomUUID().toString();
		contextMap.put(id, context);
		return id;
	}

	@Override
	public T resolve(String identifier) {
		return contextMap.get(identifier);
	}

	@Override
	public void release(String identifier) {
		contextMap.remove(identifier);
	}

}
