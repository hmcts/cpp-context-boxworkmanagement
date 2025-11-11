package uk.gov.moj.cpp.boxworkmanagement.healthchecks;

import static java.lang.String.format;
import static java.util.List.of;
import static uk.gov.justice.services.healthcheck.api.HealthcheckResult.failure;

import uk.gov.justice.services.healthcheck.api.Healthcheck;
import uk.gov.justice.services.healthcheck.api.HealthcheckResult;
import uk.gov.justice.services.healthcheck.utils.database.TableChecker;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;

public class BoxWorkManagementCamundaDatabaseHealthcheck implements Healthcheck {

    public static final String BOX_WORK_MANAGEMENT_CAMUNDA_DATABASE_HEALTHCHECK = "work-management-camunda-database-healthcheck";

    @SuppressWarnings("squid:S2386")
    public static final List<String> TABLE_NAMES = of(
            "act_id_info",
            "act_id_group",
            "act_id_membership",
            "act_id_user",
            "act_id_tenant",
            "act_id_tenant_member",
            "act_ge_property",
            "act_ru_filter",
            "act_ru_meter_log",
            "act_ru_authorization",
            "act_re_deployment",
            "act_ru_task",
            "act_ru_identitylink",
            "act_ru_event_subscr",
            "act_ru_ext_task",
            "act_ru_batch",
            "act_ru_execution",
            "act_ge_bytearray",
            "act_ru_job",
            "act_ru_jobdef",
            "act_ru_incident",
            "act_ru_variable",
            "act_ru_case_execution",
            "act_re_case_def",
            "act_ru_case_sentry_part",
            "act_re_decision_req_def",
            "act_re_decision_def",
            "act_hi_comment",
            "act_hi_detail",
            "act_hi_identitylink",
            "act_hi_taskinst",
            "act_hi_varinst",
            "act_hi_actinst",
            "act_hi_procinst",
            "act_hi_ext_task_log",
            "act_hi_op_log",
            "act_hi_attachment",
            "act_hi_incident",
            "act_hi_batch",
            "act_hi_job_log",
            "act_hi_caseinst",
            "act_hi_caseactinst",
            "act_hi_decinst",
            "act_hi_dec_in",
            "act_hi_dec_out",
            "act_re_procdef",
            "act_ge_schema_log",
            "act_ru_task_meter_log"
    );

    @Inject
    private WorkManagementJdbcDataSourceProvider workManagementJdbcDataSourceProvider;

    @Inject
    private TableChecker tableChecker;

    @SuppressWarnings("squid:S1312")
    @Inject
    private Logger logger;

    @Override
    public String getHealthcheckName() {
        return BOX_WORK_MANAGEMENT_CAMUNDA_DATABASE_HEALTHCHECK;
    }

    @Override
    public String healthcheckDescription() {
        return "Checks connectivity to the workmanagement (camunda) database and that all tables are available";
    }

    @Override
    public HealthcheckResult runHealthcheck() {
        final DataSource dataSource = workManagementJdbcDataSourceProvider.getDataSource();

        try {
            return tableChecker.checkTables(TABLE_NAMES, dataSource);

        } catch (final SQLException e) {
            logger.error("Healthcheck for workmanagement database failed.", e);
            return failure(format("Exception thrown accessing workmanagement database. %s: %s", e.getClass().getName(), e.getMessage()));
        }
    }
}
