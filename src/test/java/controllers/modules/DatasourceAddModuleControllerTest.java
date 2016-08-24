package controllers.modules;

import org.junit.Test;

import static conf.Routes.ADD_DATASOURCE_HTML;
import static conf.Routes.ADD_DATASOURCE_JS;

public class DatasourceAddModuleControllerTest extends ModuleControllerTest {
    @Test
    public void testCreateDatasourceHtml() {
        this.setUrl(ADD_DATASOURCE_HTML);
        this.assertGetHtmlPostLogin();
    }

    @Test
    public void testCreateDatasourceJs() {
        this.setUrl(ADD_DATASOURCE_JS);
        this.assertGetJsPostLogin();
    }
}
