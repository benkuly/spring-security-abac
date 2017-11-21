package net.folivo.springframework.security.abac.demo.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import net.folivo.springframework.security.abac.demo.entities.Posting;

public class PostingSerializer extends StdSerializer<Posting> {

	private static final long serialVersionUID = -7709556768147507517L;

	public PostingSerializer() {
		super(Posting.class);
	}

	@Override
	public void serialize(Posting value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeNumberField("id", value.getId());
		gen.writeStringField("content", value.getContent());
		gen.writeObjectField("creationTime", value.getCreationTime());
		gen.writeStringField("creatorUsername", value.getCreator().getUsername());
		gen.writeEndObject();

	}

}
