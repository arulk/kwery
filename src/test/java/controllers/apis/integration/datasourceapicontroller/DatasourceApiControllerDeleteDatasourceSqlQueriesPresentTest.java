package controllers.apis.integration.datasourceapicontroller;

import com.google.common.collect.ImmutableMap;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import controllers.apis.DatasourceApiController;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import fluentlenium.utils.DbUtil;
import models.Datasource;
import models.SqlQuery;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
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
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static util.Messages.DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M;
import static views.ActionResult.Status.failure;

public class DatasourceApiControllerDeleteDatasourceSqlQueriesPresentTest extends AbstractPostLoginApiTest {
    @Before
    public void setUpDatasourceApiControllerDeleteDatasourceSqlQueriesPresentTest() {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "foo")
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 1)
                                .build()
                )
        ).launch();
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                DatasourceApiController.class,
                "delete",
                ImmutableMap.of(
                        "datasourceId", 1
                )
        );

        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(failure.name())));
        assertThat(response, hasJsonPath("$.messages.length()", is(1)));
        assertThat(response, hasJsonPath("$.messages[0]", is(DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M)));
    }
}
