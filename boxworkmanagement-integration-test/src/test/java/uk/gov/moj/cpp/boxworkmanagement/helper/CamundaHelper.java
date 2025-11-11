package uk.gov.moj.cpp.boxworkmanagement.helper;


import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static uk.gov.justice.services.test.utils.core.http.RequestParamsBuilder.requestParams;
import static uk.gov.justice.services.test.utils.core.http.RestPoller.poll;
import static uk.gov.justice.services.test.utils.core.matchers.ResponsePayloadMatcher.payload;
import static uk.gov.justice.services.test.utils.core.matchers.ResponseStatusMatcher.status;

import uk.gov.justice.services.test.utils.core.http.ResponseData;
import uk.gov.justice.services.test.utils.core.rest.RestClient;

import java.io.StringReader;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.core.Response;

import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaHelper.class);

    private static final String HOST = System.getProperty("INTEGRATION_HOST_KEY", "localhost");
    private static final int PORT = 8080;

    private static final String CAMUNDA_BASE_URI = "http://" + HOST + ":" + PORT + "/engine-rest";

    public static String getProcessInstanceId(String businessKey, String processDefinitionName) {
        final String url = CAMUNDA_BASE_URI + "/history/process-instance?processInstanceBusinessKey=" + businessKey;
        final ResponseData responseData = validateAndGetResponse(url);
        JsonArray processInstances = getJsonArray(responseData.getPayload());
        final Optional<JsonValue> optionalProcessInstance = processInstances.stream().filter(ja -> ((JsonObject) ja).getString("processDefinitionName").equals(processDefinitionName)).findFirst();
        if (optionalProcessInstance.isPresent()) {
            final String processInstanceId = ((JsonObject) optionalProcessInstance.get()).getString("id");
            LOGGER.info("Process instance ID for instance with business key '{}' and process name '{}' is '{}'", businessKey, processDefinitionName, processInstanceId);
            return processInstanceId;
        }
        LOGGER.info("No process instance found with business key '{}' and process name '{}'", businessKey, processDefinitionName);
        return null;
    }

    public static String getRunningProcessInstanceId(String businessKey, String processDefinitionName) {
        final String url = CAMUNDA_BASE_URI + format("/process-instance?businessKey=%s&processDefinitionKey=%s", businessKey, processDefinitionName);
        final ResponseData responseData = validateAndGetResponse(url);
        JsonArray processInstances = getJsonArray(responseData.getPayload());
        final Optional<JsonValue> optionalProcessInstance = processInstances.stream().findFirst();

        if (optionalProcessInstance.isPresent()) {
            final String processInstanceId = ((JsonObject) optionalProcessInstance.get()).getString("id");
            LOGGER.info("Process instance ID for instance with business key '{}' and process name '{}' is '{}'", businessKey, processDefinitionName, processInstanceId);
            return processInstanceId;
        }
        LOGGER.info("No process instance found with business key '{}' and process name '{}'", businessKey, processDefinitionName);
        return null;
    }

    public static void verifyProcessFinished(final String processInstanceId) {
        final String url = CAMUNDA_BASE_URI + format("/history/process-instance/%s", processInstanceId);
        validateAndGetResponse(url, withJsonPath("$.endTime", notNullValue()));
    }

    private static ResponseData validateAndGetResponse(String url, Matcher... matchers) {

        final ResponseData response = poll(requestParams(url, "application/json")).until(
                status().is(OK),
                payload().isJson(allOf(matchers))
        );
        LOGGER.info("Issuing Rest API call to URL: {} and response is \n {} ", url, response.getPayload());
        return response;
    }

    public static void deleteProcessInstance(final String businessKey, final String processDefinitionName) {
        final String processInstanceId = getRunningProcessInstanceId(businessKey, processDefinitionName);
        if (processInstanceId != null) {
            deleteProcessInstance(processInstanceId);
        } else {
            LOGGER.warn("No process instance exists for [{}] and [{}]", processDefinitionName, businessKey);
        }
    }

    public static void deleteProcessInstance(final String processInstanceId) {
        final String url = CAMUNDA_BASE_URI + format("/process-instance/%s", processInstanceId);
        final RestClient restClient = new RestClient();
        final Response response = restClient.deleteCommand(url, null, null);

        if (HTTP_NO_CONTENT == response.getStatus()) {
            LOGGER.info("ProcessInstance deleted [{}]", processInstanceId);
        } else {
            LOGGER.warn("Could not delete ProcessInstance [{}], [{}]", processInstanceId, response.getStatus());
        }
    }

    public static void deleteAllProcessInstances() {
        final ResponseData responseData = getAllProcessInstances();
        final JsonArray processInstances = Json.createReader(new StringReader(responseData.getPayload())).readArray();
        LOGGER.info("ProcessInstances={}", processInstances);
        processInstances.stream().forEach(pi -> deleteProcessInstance(((JsonObject) pi).getString("id")));
    }

    public static ResponseData getAllProcessInstances() {
        final String url = CAMUNDA_BASE_URI + "/process-instance?processDefinitionKey=c2i_case_overview";
        return validateAndGetResponse(url);
    }

    private static JsonArray getJsonArray(String payload) {
        try (JsonReader reader = Json.createReader(new StringReader(payload))) {
            return reader.readArray();
        }
    }
}
