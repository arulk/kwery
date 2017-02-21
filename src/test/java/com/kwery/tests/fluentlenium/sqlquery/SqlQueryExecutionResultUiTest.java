package com.kwery.tests.fluentlenium.sqlquery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;

import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SqlQueryExecutionResultUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected SqlQueryExecutionResultPage page;
    protected String jsonResult;

    @Before
    public void setUpSqlQueryExecutionResultTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        jsonResult = objectMapper.writeValueAsString(
                ImmutableList.of(
                        ImmutableList.of("username", "password"),
                        ImmutableList.of("raju", "cool"),
                        ImmutableList.of("kaju", "dude")
                )
        );

        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_EXECUTION_ERROR, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, "thik-3456-lkdsjkfkl-lskjdfkl", 1475158740747l, jsonResult, SUCCESS, sqlQueryModel.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .build()
                )
        );
        dbSetup.launch();

        page = newInstance(SqlQueryExecutionResultPage.class);
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list SQL queries execution page");
        }
    }

    @Test
    public void test() {
        page.waitForResultHeader(2);
        assertThat(page.resultHeader(), hasSize(2));

        List<String> resultHeaders = page.resultHeader();

        assertThat(resultHeaders.get(0), is("username"));
        assertThat(resultHeaders.get(1), is("password"));

        List<String> firstRow = page.resultContent().get(0);

        assertThat(firstRow.get(0), is("raju"));
        assertThat(firstRow.get(1), is("cool"));

        List<String> secondRow = page.resultContent().get(1);

        assertThat(secondRow.get(0), is("kaju"));
        assertThat(secondRow.get(1), is("dude"));
    }
}

