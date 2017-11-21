package net.folivo.springframework.security.abac.demo.jackson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.util.ReflectionUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import net.folivo.springframework.security.abac.demo.entities.Posting;
import net.folivo.springframework.security.abac.demo.entities.StdUserRepository;

public class PostingDeserializer extends StdDeserializer<Posting> {

	private static final long serialVersionUID = 2579791635779338932L;
	private final StdUserRepository repo;

	public PostingDeserializer(StdUserRepository repo) {
		super(Posting.class);
		this.repo = repo;
	}

	@Override
	public Posting deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		Posting posting = ReflectionUtils.createInstanceIfPresent(Posting.class.getName(),
				new Posting(null, null, null));
		List<String> parsedValues = Arrays.asList("creatorUsername", "creationTime", "content");
		while (p.nextToken() != JsonToken.END_OBJECT) {
			String name = p.getCurrentName();
			Field field = null;
			Object value = null;
			switch (name) {
			case "creatorUsername":
				field = ReflectionUtils.findRequiredField(Posting.class, "creator");
				value = repo.findByUsernameIgnoreCase(p.getText());
				break;
			case "creationTime":
				field = ReflectionUtils.findRequiredField(Posting.class, "creationTime");
				value = p.readValueAs(field.getDeclaringClass());
				break;
			case "content":
				field = ReflectionUtils.findRequiredField(Posting.class, "content");
				value = p.getText();
				break;
			}
			if (value != null && field != null) {
				ReflectionUtils.setField(field, posting, value);
				parsedValues.remove(name);
			}
		}
		if (parsedValues.isEmpty())
			return posting;
		else
			throw new JsonParseException(p, "Posting has missing attributes");
	}

}
