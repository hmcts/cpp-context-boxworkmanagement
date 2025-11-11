package uk.gov.moj.cpp.boxworkmanagement.event.processor.delegate;

import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("createBoxworkTask")
public class CreateBoxworkTask implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateBoxworkTask.class);

    private static final String CASE_ID_ATTRIBUTE = "caseId";

    @Override
    public void execute(DelegateExecution execution) {

        final String caseId = (String) execution.getVariable(CASE_ID_ATTRIBUTE);

        LOGGER.info("Boxwork Application Created for case id {}", caseId);

    }

}
