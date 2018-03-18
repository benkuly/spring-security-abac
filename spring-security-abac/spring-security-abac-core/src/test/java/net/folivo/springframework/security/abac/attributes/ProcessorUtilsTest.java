package net.folivo.springframework.security.abac.attributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import name.falgout.jeffrey.testing.junit5.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProcessorUtilsTest {

	@Mock
	private RequestAttribute attribute1;
	@Mock
	private RequestAttribute attribute2;
	private Stream<RequestAttribute> attributes;

	@Mock
	private RequestAttributeProcessor<Object> processor1;
	@Mock
	private RequestAttributeProcessor<Object> processor2;
	private Collection<RequestAttributeProcessor<Object>> processors;

	@Mock
	private Object context;

	@BeforeEach
	private void intiCollections() {
		processors = new ArrayList<>();
		attributes = Stream.empty();
	}

	/*
	 * empty attributes collection should return empty attributes collection
	 */
	@Test
	public void processWithoutAttributes() {
		processors.add(processor1);

		when(processor1.supports(any(RequestAttribute.class))).thenReturn(true);
		when(processor1.process(any(RequestAttribute.class), any())).thenReturn(Stream.of(attribute1));

		Stream<RequestAttribute> processed = ProcessorUtils.process(attributes, context, processors);
		assertThat(processed).isEmpty();
	}

	/*
	 * attribute should be processed by only one applicable processor
	 */
	@Test
	public void processWithOnlyOneApplicableProcessor() {
		processors.add(processor1);
		processors.add(processor2);
		attributes = Stream.of(attribute1);

		when(processor1.supports(any(RequestAttribute.class))).thenReturn(true);
		when(processor1.process(any(RequestAttribute.class), any())).thenReturn(Stream.of(attribute1));
		when(processor2.supports(any(RequestAttribute.class))).thenReturn(true);
		verify(processor2, never()).process(any(RequestAttribute.class), any());

		ProcessorUtils.process(attributes, context, processors);
	}

	/*
	 * attribute should be passed through if there is no processor
	 */
	@Test
	public void processWithNoProcessor() {
		attributes = Stream.of(attribute1);

		Stream<RequestAttribute> processed = ProcessorUtils.process(attributes, context, processors);
		assertThat(processed).containsOnly(attribute1);
	}

	/*
	 * attribute should be passed through if there is no applicable processor
	 */
	@Test
	public void processWithNoApplicableProcessor() {
		processors.add(processor1);
		attributes = Stream.of(attribute1);

		when(processor1.supports(any(RequestAttribute.class))).thenReturn(false);

		Stream<RequestAttribute> processed = ProcessorUtils.process(attributes, context, processors);
		assertThat(processed).containsOnly(attribute1);
	}

	/*
	 * all by processor returned attributes should be contained
	 */
	@Test
	public void processContainsAttributes() {
		processors.add(processor1);
		attributes = Stream.of(attribute1);

		when(processor1.supports(any(RequestAttribute.class))).thenReturn(true);
		when(processor1.process(any(RequestAttribute.class), any())).thenReturn(Stream.of(attribute1, attribute2));

		Stream<RequestAttribute> processed = ProcessorUtils.process(attributes, context, processors);
		assertThat(processed).containsOnly(attribute1, attribute2);
	}

	/*
	 * multiple attributes should be processed
	 */
	@Test
	public void processAllAttributes() {
		processors.add(processor1);
		processors.add(processor2);
		attributes = Stream.of(attribute1, attribute2);
		RequestAttribute processedAttribute = mock(RequestAttribute.class);

		when(processor1.supports(any(RequestAttribute.class))).thenReturn(true, false);
		when(processor1.process(any(RequestAttribute.class), any())).thenReturn(Stream.of(attribute1));
		when(processor2.supports(any(RequestAttribute.class))).thenReturn(true, true);
		when(processor2.process(any(RequestAttribute.class), any()))
				.thenReturn(Stream.of(attribute2, processedAttribute));

		Stream<RequestAttribute> processed = ProcessorUtils.process(attributes, context, processors);
		assertThat(processed).containsOnly(attribute1, attribute2, processedAttribute);
	}
}
