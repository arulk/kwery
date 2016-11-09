package controllers.apis.integration.sqlqueryapicontroller;

import com.google.common.collect.ImmutableMap;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import controllers.apis.SqlQueryApiController;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.SqlQueryDao;
import models.Datasource;
import models.SqlQuery;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.SchedulerService;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static java.text.MessageFormat.format;
import static models.Datasource.COLUMN_ID;
import static models.Datasource.COLUMN_LABEL;
import static models.Datasource.COLUMN_PASSWORD;
import static models.Datasource.COLUMN_PORT;
import static models.Datasource.COLUMN_TYPE;
import static models.Datasource.COLUMN_URL;
import static models.Datasource.COLUMN_USERNAME;
import static models.Datasource.Type.MYSQL;
import static models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static models.SqlQuery.COLUMN_QUERY;
import static models.SqlQuery.TABLE;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static util.Messages.SQL_QUERY_DELETE_SUCCESS_M;
import static views.ActionResult.Status.success;

public class SqlQueryApiControllerDeleteTest extends AbstractPostLoginApiTest {
    protected SchedulerService schedulerService;

    @Before
    public void testSqlQueryApiControllerDeleteTest() {
        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "foo")
                                .build(),
                        insertInto(TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 1)
                                .build()
                )
        ).launch();

        schedulerService = getInjector().getInstance(SchedulerService.class);
        schedulerService.schedule(getInjector().getInstance(SqlQueryDao.class).getById(1));
    }

    @Test
    public void test() throws InterruptedException {
        TimeUnit.SECONDS.sleep(70);

        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "delete",
                ImmutableMap.of(
                        "sqlQueryId", 1
                )
        );

        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages.length()", is(1)));
        assertThat(response, hasJsonPath("$.messages[0]", is(format(SQL_QUERY_DELETE_SUCCESS_M, "testQuery0"))));

        assertThat(schedulerService.getQueryRunSchedulerMap().get(1), nullValue());
    }
}