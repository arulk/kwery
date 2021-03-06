package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.models.Datasource.Type;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
import org.openqa.selenium.support.FindBy;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kwery.models.Datasource.Type.POSTGRESQL;
import static com.kwery.models.Datasource.Type.REDSHIFT;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;
import static org.fluentlenium.core.filter.FilterConstructor.withTextContent;
import static org.openqa.selenium.By.className;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
public class DatasourceAddPage extends KweryFluentPage implements RepoDashPage {
    @Wait(timeout = TIMEOUT_SECONDS, timeUnit = SECONDS)
    @FindBy(id = "addDatasourceForm")
    protected FluentWebElement form;

    protected boolean onboardingFlow;

    protected ActionResultComponent actionResultComponent;

    public void submitForm(Datasource datasource) {
        $(".type-f").fillSelect().withText(datasource.getType().name());

        if (datasource.getType() == POSTGRESQL || datasource.getType() == REDSHIFT) {
            waitForDatabaseFormFieldToBeVisible();
            $(".database-f").fill().with(datasource.getDatabase());
        }

        $(".url-f").fill().with(datasource.getUrl());
        $(".port-f").fill().with(String.valueOf(datasource.getPort()));
        $(".username-f").fill().with(datasource.getUsername());
        $(".password-f").fill().with(datasource.getPassword());
        $(".label-f").fill().with(datasource.getLabel());

        $("#create").click();
    }

    public void submitForm() {
        $(".save-datasource-f").click();
    }

    @Override
    public boolean isRendered() {
        return form.displayed();
    }

    public void waitForSuccessMessage(String label, Type type) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-success-message p")).text(format(DATASOURCE_ADDITION_SUCCESS_M, type.name(), label));
    }

    public void waitForFailureMessage(String label, Type type) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-failure-message p")).text(format(DATASOURCE_ADDITION_FAILURE_M, type.name(), label));
    }

    public void waitForFailureMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-failure-message")).displayed();
    }

    public List<String> errorMessages() {
        return $(".f-failure-message p").stream().map(FluentWebElement::text).collect(Collectors.toCollection(LinkedList::new));
    }

    public void assertFormValidationMessagePresent(FormField formField) {
        assertThat(el("div", withClass().contains(String.format("%s-error-f", formField)), withTextContent().notContains("")));
    }

    public void waitForDatabaseFormFieldToBeVisible() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".database-f")).displayed();
    }

    public void waitForDatabaseFormFieldToBeInvisible() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".database-f")).not().displayed();
    }

    public boolean isDatabaseFormFieldVisible() {
        return $(className("database-f")).first().displayed();
    }

    public void selectDatasourceType(Type type) {
        $(".type-f").fillSelect().withText(type.name());
    }

    public enum FormField {
        type, url, database, port, username, password, label
    }

    public void waitForDatasourceListPage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> getDriver().getCurrentUrl().equals(getBaseUrl() + "/#datasource/list"));
    }

    public void waitForReportAddPage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> getDriver().getCurrentUrl().equals(getBaseUrl() + "/#report/add?onboarding=true&fromDatasource=true"));
    }

    public void setOnboardingFlow(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }

    @Override
    public String getUrl() {
        if (onboardingFlow) {
            return "/#datasource/add?onboarding=true";
        } else {
            return "/#datasource/add";
        }
    }

    public ActionResultComponent getActionResultComponent() {
        return actionResultComponent;
    }
}
