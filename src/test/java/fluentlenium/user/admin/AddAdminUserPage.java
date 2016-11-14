package fluentlenium.user.admin;

import fluentlenium.RepoDashPage;
import models.User;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import static fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static util.Messages.ADMIN_USER_ADDITION_NEXT_STEP_M;
import static util.Messages.ADMIN_USER_ADDITION_SUCCESS_M;

public class AddAdminUserPage extends FluentPage implements RepoDashPage {
    @AjaxElement
    @FindBy(id = "createAdminUserForm")
    protected FluentWebElement createAdminUserForm;

    @Override
    public String getUrl() {
        return "/#user/add";
    }

    public void submitForm(String... inputs) {
        fill("input").with(inputs);
        click("#createAdminUser");
    }

    public void submitForm() {
        fill("input").with();
        click("#createAdminUser");
    }

    public String usernameValidationErrorMessage() {
        return $("#username-error").getText();
    }

    public String passwordValidationErrorMessage() {
        return $("#password-error").getText();
    }

    @Override
    public boolean isRendered() {
        return createAdminUserForm.isDisplayed();
    }

    public void waitForSuccessMessage(User user) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(format(ADMIN_USER_ADDITION_SUCCESS_M, user.getUsername()));
    }

    public void foo(String username) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until("#actionResultDialog p").hasText(format(ADMIN_USER_ADDITION_SUCCESS_M, username));
    }

    public void waitForFailureMessage(User user) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-failure-message p").hasText(format(ADMIN_USER_ADDITION_FAILURE_M, user.getUsername()));
    }

    public String nextActionName() {
        return $("#nextAction").getText();
    }

    public String expectedNextActionName() {
        return ADMIN_USER_ADDITION_NEXT_STEP_M.toUpperCase();
    }

    public void clickNextAction() {
        click("#nextAction");
    }

    public String expectedNextActionUrl() {
        return getBaseUrl() + "/#user/login";
    }

    public void waitForNextPage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until("#loginForm").isPresent();
    }
}
