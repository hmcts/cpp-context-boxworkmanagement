package uk.gov.moj.cpp.boxworkmanagement.healthchecks;

import static java.lang.String.format;

import uk.gov.justice.services.jdbc.persistence.JdbcRepositoryException;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class WorkManagementJdbcDataSourceProvider {

    @SuppressWarnings("squid:S2386")
    public static final String DATA_SOURCE_NAME = "java:/DS.workmanagement";

    @Inject
    private InitialContext initialContext;

    public synchronized DataSource getDataSource() {
        try {
            return (DataSource) initialContext.lookup(DATA_SOURCE_NAME);
        } catch (final NamingException e) {
            throw new JdbcRepositoryException(format("Failed to lookup workmanagement DataSource using JNDI name '%s'", DATA_SOURCE_NAME), e);
        }
    }
}
