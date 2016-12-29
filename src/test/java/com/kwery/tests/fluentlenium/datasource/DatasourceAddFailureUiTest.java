package com.kwery.tests.fluentlenium.datasource;

import com.amazonaws.services.elastictranscoder.model.transform.TestRoleResultJsonUnmarshaller;
import com.kwery.models.Datasource;
import com.kwery.tests.util.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.dbId;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.DATASOURCE_CONNECTION_FAILURE_M;
import static com.kwery.tests.util.TestUtil.datasource;
import static java.text.MessageFormat.format;
import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class DatasourceAddFailureUiTest extends ChromeFluentTest {
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected DatasourceAddPage page;

    protected Datasource datasource;

    @Before
    public void setUpAddDatasourceFailureTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());

        datasourceDbSetup(datasource);

        page = createPage(DatasourceAddPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render update datasource page");
        }
    }

    @Test
    public void test() {
        Datasource newDatasource = datasource();
        newDatasource.setLabel(datasource.getLabel());

        page.submitForm(newDatasource);
        page.waitForFailureMessage();
        assertThat(
                page.errorMessages(),
                containsInAnyOrder(
                        format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, newDatasource.getLabel()),
                        format(DATASOURCE_CONNECTION_FAILURE_M, newDatasource.getType().name())
                )
        );
    }
}
