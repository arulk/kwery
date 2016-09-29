package fluentlenium.queryrun;

import org.junit.Test;
import util.Messages;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class AddQueryRunValidationTest extends QueryRunTest {
    @Test
    public void test() {
        page.submitForm();
        assertThat(page.validationMessages(), containsInAnyOrder(
                Messages.QUERY_VALIDATION_M,
                Messages.CRON_EXPRESSION_VALIDATION_M,
                Messages.LABEL_VALIDATION_M
        ));
    }
}
