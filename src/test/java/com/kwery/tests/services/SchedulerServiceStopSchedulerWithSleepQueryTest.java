package com.kwery.tests.services;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.tests.util.MySqlDocker;
import com.kwery.tests.util.RepoDashTestBase;

import java.util.concurrent.TimeUnit;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static com.kwery.models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static com.kwery.models.SqlQuery.COLUMN_QUERY;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class SchedulerServiceStopSchedulerWithSleepQueryTest extends RepoDashTestBase {
    protected MySqlDocker mySqlDocker;
    protected SchedulerService schedulerService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    @Before
    public void setUpSchedulerServiceStopSchedulerWithRegularQueryTest() {
        mySqlDocker = new MySqlDocker();
        mySqlDocker.start();

        Datasource datasource = mySqlDocker.datasource();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery0", "select sleep(84000)", 1)
                                .build()
                )
        ).launch();

        schedulerService = getInstance(SchedulerService.class);
        schedulerService.schedule(getInstance(SqlQueryDao.class).getById(1));

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() throws InterruptedException {
        TimeUnit.SECONDS.sleep(70);
        schedulerService.stopScheduler(1);

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(1);

        long countBeforeSleep = sqlQueryExecutionDao.count(filter);
        TimeUnit.SECONDS.sleep(70);
        long countAfterSleep = sqlQueryExecutionDao.count(filter);

        assertThat(schedulerService.getQueryRunSchedulerMap().get(1), nullValue());
        assertThat("No new query was executed after stopping the scheduler", countBeforeSleep, is(countAfterSleep));
    }

    @After
    public void tearDownSchedulerServiceStopSchedulerWithRegularQueryTest() {
        mySqlDocker.tearDown();
    }
}