package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kwery.controllers.apis.SqlQueryApiController;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.COLUMN_QUERY_RUN_ID_FK;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.models.SqlQueryModel.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryApiControllerExecutionResultTest extends AbstractPostLoginApiTest {
    protected String jsonResult;
    protected String executionId = "executionId";

    @Before
    public void beforeSqlQueryExecutionResultTest () throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        jsonResult = objectMapper.writeValueAsString(
                ImmutableList.of(
                        ImmutableList.of("header"),
                        ImmutableList.of("value")
                )
        );

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, CRON_EXPRESSION_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 1).build(),
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, executionId, 1475158740747l, jsonResult, SUCCESS, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .build()
                )
        );
        dbSetup.launch();
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "sqlQueryExecutionResult",
                ImmutableMap.of(
                        "sqlQueryId", 1,
                        "sqlQueryExecutionId", executionId
                )
        );

        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(url));
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);

        assertThat(json, isJson());
        assertThat(JsonPath.read(json, "$[0].length()"), is(1));
        assertThat(json, hasJsonPath("$[0].[0]", is("header")));

        assertThat(JsonPath.read(json, "$[1].length()"), is(1));
        assertThat(json, hasJsonPath("$[1].[0]", is("value")));
    }

    @Test
    public void testNonExistent() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "sqlQueryExecutionResult",
                ImmutableMap.of(
                        "sqlQueryId", 1,
                        "sqlQueryExecutionId", executionId + "foo"
                )
        );

        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(jsonResponse, is("[]"));
    }
}
