package controllers.routetest;

import com.google.common.collect.ImmutableMap;
import conf.Routes;
import org.junit.Test;

import static conf.Routes.ONBOARDING_ADD_DATASOURCE_HTML;
import static conf.Routes.ONBOARDING_ADD_DATASOURCE_JS;

public class DatasourceControllerRouteTest extends RouteTest {
    @Test
    public void testAddDatasource() {
        this.setUrl(Routes.ONBOARDING_ADD_DATASOURCE);
        this.setPostParams(
                ImmutableMap.of(
                        "url", "url",
                        "username", "purvi",
                        "password", "password",
                        "label", "test",
                        "type", "MYSQL"
                )
        );
        this.assertPostJson();
    }

    @Test
    public void testCreateDatasourceHtml() {
        this.setUrl(ONBOARDING_ADD_DATASOURCE_HTML);
        this.assertGetHtml();
    }

    @Test
    public void testCreateDatasourceJs() {
        this.setUrl(ONBOARDING_ADD_DATASOURCE_JS);
        this.assertGetJs();
    }
}
