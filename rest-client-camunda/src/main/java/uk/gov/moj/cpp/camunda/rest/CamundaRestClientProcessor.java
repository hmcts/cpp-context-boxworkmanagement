package uk.gov.moj.cpp.camunda.rest;

import uk.gov.justice.services.clients.core.DefaultRestClientProcessor;
import uk.gov.justice.services.clients.core.EndpointDefinition;
import uk.gov.justice.services.common.configuration.Value;
import uk.gov.justice.services.messaging.JsonEnvelope;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

/**
 * Extension of the {@link DefaultRestClientProcessor} that provides support for overwriting the baseUri
 * of all Rest requests with a JNDI configuraable address.
 */
@Alternative
public class CamundaRestClientProcessor extends DefaultRestClientProcessor {

    @Inject
    @Value(key = "camunda.rest.baseUri", defaultValue = "localhost")
    private String baseUri;

    /**
     * Make a synchronous GET request using the envelope provided to a specified endpoint.
     *
     * @param definition the endpoint definition
     * @param envelope   the envelope containing the payload and/or parameters to pass in the
     *                   request
     * @return the response that the endpoint returned for this request
     */
    @Override
    public JsonEnvelope get(final EndpointDefinition definition, final JsonEnvelope envelope) {
        return super.get(getDefinitionWithUpdatedBaseUri(definition), envelope);
    }

    /**
     * Make an asynchronous POST request using the envelope provided to a specified endpoint.
     *
     * @param definition the endpoint definition
     * @param envelope   the envelope containing the payload and/or parameters to pass in the
     *                   request
     */
    @Override
    public void post(final EndpointDefinition definition, final JsonEnvelope envelope) {
        super.post(getDefinitionWithUpdatedBaseUri(definition), envelope);
    }

    /**
     * Make a synchronous POST request using the envelope provided to a specified endpoint.
     *
     * @param definition the endpoint definition
     * @param envelope   the envelope containing the payload and/or parameters to pass in the
     *                   request
     * @return the response that the endpoint returned for this request
     */
    @Override
    public JsonEnvelope synchronousPost(final EndpointDefinition definition, final JsonEnvelope envelope) {
        return super.synchronousPost(getDefinitionWithUpdatedBaseUri(definition), envelope);
    }

    /**
     * Make an asynchronous PUT request using the envelope provided to a specified endpoint.
     *
     * @param definition the endpoint definition
     * @param envelope   the envelope containing the payload and/or parameters to pass in the
     *                   request
     */
    @Override
    public void put(final EndpointDefinition definition, final JsonEnvelope envelope) {
        super.put(getDefinitionWithUpdatedBaseUri(definition), envelope);
    }

    /**
     * Make a synchronous PUT request using the envelope provided to a specified endpoint.
     *
     * @param definition the endpoint definition
     * @param envelope   the envelope containing the payload and/or parameters to pass in the
     *                   request
     * @return the response that the endpoint returned for this request
     */
    @Override
    public JsonEnvelope synchronousPut(final EndpointDefinition definition, final JsonEnvelope envelope) {
        return super.synchronousPut(getDefinitionWithUpdatedBaseUri(definition), envelope);
    }

    /**
     * Make an asynchronous PATCH request using the envelope provided to a specified endpoint.
     *
     * @param definition the endpoint definition
     * @param envelope   the envelope containing the payload and/or parameters to pass in the
     *                   request
     */
    @Override
    public void patch(final EndpointDefinition definition, final JsonEnvelope envelope) {
        super.patch(getDefinitionWithUpdatedBaseUri(definition), envelope);
    }

    /**
     * Make a synchronous PATCH request using the envelope provided to a specified endpoint.
     *
     * @param definition the endpoint definition
     * @param envelope   the envelope containing the payload and/or parameters to pass in the
     *                   request
     * @return the response that the endpoint returned for this request
     */
    @Override
    public JsonEnvelope synchronousPatch(final EndpointDefinition definition, final JsonEnvelope envelope) {
        return super.synchronousPatch(getDefinitionWithUpdatedBaseUri(definition), envelope);
    }

    /**
     * Make an asynchronous DELETE request using the envelope provided to a specified endpoint.
     *
     * @param definition the endpoint definition
     * @param envelope   the envelope containing the payload and/or parameters to pass in the
     *                   request
     */
    @Override
    public void delete(final EndpointDefinition definition, final JsonEnvelope envelope) {
        super.delete(getDefinitionWithUpdatedBaseUri(definition), envelope);
    }

    private EndpointDefinition getDefinitionWithUpdatedBaseUri(final EndpointDefinition definition) {
        return new EndpointDefinition(definition.getBaseUri().replace("localhost", baseUri), definition.getPath(), definition.getPathParams(), definition.getQueryParams(), definition.getResponseMediaType());
    }

}
