package uk.gov.moj.cpp.camunda.rest;

import static java.util.Collections.emptySet;
import static javax.json.Json.createObjectBuilder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUID;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;

import uk.gov.justice.services.clients.core.EndpointDefinition;
import uk.gov.justice.services.clients.core.webclient.WebTargetFactory;
import uk.gov.justice.services.clients.core.webclient.WebTargetFactoryFactory;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.JsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.logging.TraceLogger;
import uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory;

import java.time.ZonedDateTime;

import javax.json.Json;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CamundaRestClientProcessorTest {

    private static final String RESPONSE_MEDIA_TYPE = "test";
    private static final String BASE_URI_OVERRIDE = "10.0.0.10";

    @Spy
    private Enveloper enveloper = EnveloperFactory.createEnveloper();

    @Spy
    private StringToJsonObjectConverter stringToJsonObjectConverter;

    @Mock
    private JsonObjectEnvelopeConverter jsonObjectEnvelopeConverter;

    @Mock
    private WebTargetFactoryFactory webTargetFactoryFactory;

    @Mock
    private WebTargetFactory webTargetFactory;

    @Mock
    private TraceLogger traceLogger;

    @InjectMocks
    private CamundaRestClientProcessor camundaRestClientProcessor;

    private JsonEnvelope envelope;

    private EndpointDefinition endpointDefinition;

    @Mock
    private WebTarget target;

    @Mock
    private Invocation.Builder builder;

    @Captor
    private ArgumentCaptor<EndpointDefinition> definitionArgumentCaptor;

    @Captor
    private ArgumentCaptor<Entity> entityArgumentCaptor;

    @BeforeEach
    public void setup() {
        setField(camundaRestClientProcessor, "baseUri", BASE_URI_OVERRIDE);

        endpointDefinition = new EndpointDefinition("http://localhost:8080", "/somePath", emptySet(), emptySet(), RESPONSE_MEDIA_TYPE);

        envelope = envelopeFrom(
                metadataWithRandomUUID("test").createdAt(ZonedDateTime.now()),
                createObjectBuilder().add("name", "value").build());

        when(webTargetFactoryFactory.create()).thenReturn(webTargetFactory);
        when(webTargetFactory.createWebTarget(definitionArgumentCaptor.capture(), eq(envelope))).thenReturn(target);
        when(target.request("application/vnd.test+json")).thenReturn(builder);
    }

    @Test
    public void shouldExecuteGetWithSubstitutedBaseUri() {
        final Response mockedResponse = mockSynchronousResponse();
        when(builder.get()).thenReturn(mockedResponse);

        camundaRestClientProcessor.get(endpointDefinition, envelope);

        assertThat(definitionArgumentCaptor.getValue().getBaseUri(), is("http://" + BASE_URI_OVERRIDE + ":8080"));
    }

    @Test
    public void shouldExecutePostWithSubstitutedBaseUri() {
        when(builder.post(entityArgumentCaptor.capture())).thenReturn(Response.accepted().build());
        camundaRestClientProcessor.post(endpointDefinition, envelope);

        assertThat(definitionArgumentCaptor.getValue().getBaseUri(), is("http://" + BASE_URI_OVERRIDE + ":8080"));
        assertThat(entityArgumentCaptor.getValue().getMediaType().toString(), is("application/vnd.test+json"));
        assertThat(entityArgumentCaptor.getValue().getEntity().toString(), is("{\"name\":\"value\"}"));
    }

    @Test
    public void shouldExecuteSynchronousPostWithSubstitutedBaseUri() {
        final Response mockedResponse = mockSynchronousResponse();
        when(builder.post(entityArgumentCaptor.capture())).thenReturn(mockedResponse);

        camundaRestClientProcessor.synchronousPost(endpointDefinition, envelope);

        assertThat(definitionArgumentCaptor.getValue().getBaseUri(), is("http://" + BASE_URI_OVERRIDE + ":8080"));
        assertThat(entityArgumentCaptor.getValue().getMediaType().toString(), is("application/vnd.test+json"));
        assertThat(entityArgumentCaptor.getValue().getEntity().toString(), is("{\"name\":\"value\"}"));
    }

    @Test
    public void shouldExecutePutWithSubstitutedBaseUri() {
        when(builder.put(entityArgumentCaptor.capture())).thenReturn(Response.accepted().build());
        camundaRestClientProcessor.put(endpointDefinition, envelope);

        assertThat(definitionArgumentCaptor.getValue().getBaseUri(), is("http://" + BASE_URI_OVERRIDE + ":8080"));
        assertThat(entityArgumentCaptor.getValue().getMediaType().toString(), is("application/vnd.test+json"));
        assertThat(entityArgumentCaptor.getValue().getEntity().toString(), is("{\"name\":\"value\"}"));
    }

    @Test
    public void shouldExecuteSynchronousPutWithSubstitutedBaseUri() {
        final Response mockedResponse = mockSynchronousResponse();
        when(builder.put(entityArgumentCaptor.capture())).thenReturn(mockedResponse);

        camundaRestClientProcessor.synchronousPut(endpointDefinition, envelope);

        assertThat(definitionArgumentCaptor.getValue().getBaseUri(), is("http://" + BASE_URI_OVERRIDE + ":8080"));
        assertThat(entityArgumentCaptor.getValue().getMediaType().toString(), is("application/vnd.test+json"));
        assertThat(entityArgumentCaptor.getValue().getEntity().toString(), is("{\"name\":\"value\"}"));
    }

    @Test
    public void shouldExecuteDeleteWithSubstitutedBaseUri() {
        when(builder.delete()).thenReturn(Response.accepted().build());

        camundaRestClientProcessor.delete(endpointDefinition, envelope);

        assertThat(definitionArgumentCaptor.getValue().getBaseUri(), is("http://" + BASE_URI_OVERRIDE + ":8080"));
    }

    private Response mockSynchronousResponse() {
        final Response mockedResponse = mock(Response.class);
        when(mockedResponse.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(mockedResponse.readEntity(String.class)).thenReturn(Json.createObjectBuilder().add("_metadata", "").build().toString());
        return mockedResponse;
    }

}
