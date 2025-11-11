package uk.gov.moj.cpp.boxworkmanagement.it;

import static java.util.UUID.randomUUID;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.moj.cpp.boxworkmanagement.helper.CamundaHelper.getRunningProcessInstanceId;
import static uk.gov.moj.cpp.boxworkmanagement.helper.JMSTopicHelper.postMessageToStartBoxwork;
import static uk.gov.moj.cpp.boxworkmanagement.helper.JMSTopicHelper.verifyMessageToStartAndCompletedBoxwork;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class BoxworkProcessIT {
    private static final String BOXWORKAPPLICATION =  "boxworkApplication";
    private static final String PUBLIC_HEARING_RESULTED = "public.events.hearing.hearing-resulted";
    private static final String PUBLIC_PROGRESSION_BOXWORK_APPLICATION_REFERRED = "public.progression.boxwork-application-referred";

    @Test
    public void shouldStartBoxworkApplicationWhenEventReceived() throws IOException {
        final String applicationBusinessKey =  randomUUID().toString();
        String boxworkApplicationGeneratedPayload = readFileToString(new File(this.getClass().getClassLoader().getResource("external/public.progression.boxwork-application-referred.json").getFile()));
        boxworkApplicationGeneratedPayload = boxworkApplicationGeneratedPayload.replace("URN_DEFAULT_VALUE", applicationBusinessKey);

        postMessageToStartBoxwork(boxworkApplicationGeneratedPayload, PUBLIC_PROGRESSION_BOXWORK_APPLICATION_REFERRED);

        final String processInstanceId = getRunningProcessInstanceId(applicationBusinessKey,BOXWORKAPPLICATION) ;

        assertNotNull("ProcessInstanceId should not be null", processInstanceId);

    }

    @Test
    public void shouldNudgeBoxworkApplicationWhenEventReceived() throws IOException {

        String boxworkApplicationPayload1 = readFileToString(new File(this.getClass().getClassLoader().getResource("external/public.progression.boxwork-application-referred.json").getFile()));
        String boxworkApplicationPayload2 = readFileToString(new File(this.getClass().getClassLoader().getResource("external/public.events.hearing.hearing-resulted.json").getFile()));

        verifyMessageToStartAndCompletedBoxwork(boxworkApplicationPayload1, boxworkApplicationPayload2, PUBLIC_PROGRESSION_BOXWORK_APPLICATION_REFERRED,PUBLIC_HEARING_RESULTED);

    }

}
