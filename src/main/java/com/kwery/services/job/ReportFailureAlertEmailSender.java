package com.kwery.services.job;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.kwery.dao.DomainConfigurationDao;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.UrlConfiguration;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.MailService;
import ninja.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.kwery.controllers.MessageKeys.REPORT_GENERATION_FAILURE_ALERT_EMAIL_SUBJECT;

@Singleton
public class ReportFailureAlertEmailSender {
    protected Logger logger = LoggerFactory.getLogger(ReportFailureAlertEmailSender.class);

    protected MailService mailService;
    protected DomainConfigurationDao domainConfigurationDao;
    protected Provider<KweryMail> kweryMailProvider;
    protected Messages messages;
    protected final ITemplateEngine templateEngine;

    @Inject
    public ReportFailureAlertEmailSender(MailService mailService, DomainConfigurationDao domainConfigurationDao, Provider<KweryMail> kweryMailProvider,
                                       ITemplateEngine templateEngine,  Messages messages) {
        this.mailService = mailService;
        this.domainConfigurationDao = domainConfigurationDao;
        this.kweryMailProvider = kweryMailProvider;
        this.templateEngine = templateEngine;
        this.messages = messages;
    }

    public void send(JobExecutionModel jobExecutionModel) {
        KweryMail kweryMail = kweryMailProvider.get();

        JobModel jobModel = jobExecutionModel.getJobModel();

        String subjectPrefix = messages.get(REPORT_GENERATION_FAILURE_ALERT_EMAIL_SUBJECT, Optional.absent()).get();

        String subject = subjectPrefix + " - " + jobExecutionModel.getJobModel().getTitle()
                + " - " + new SimpleDateFormat("EEE MMM dd yyyy HH:mm").format(new Date(jobExecutionModel.getExecutionStart()));
        kweryMail.setSubject(subject);

        List<UrlConfiguration> configurations = domainConfigurationDao.get();

        UrlConfiguration urlConfiguration = null;
        if (!configurations.isEmpty()) {
            urlConfiguration = domainConfigurationDao.get().get(0);
        }

        Context context = new Context();

        if (urlConfiguration!= null) {
            String url = urlConfiguration.getScheme() + "://" + urlConfiguration.getDomain() + ":" + urlConfiguration.getPort()
                    + String.format("/#report/%d/execution/%s", jobExecutionModel.getJobModel().getId(), jobExecutionModel.getExecutionId());
            context.setVariable("title", jobExecutionModel.getJobModel().getTitle());
            context.setVariable("url", url);
        }

        kweryMail.setBodyHtml(templateEngine.process("alert", context));

        jobExecutionModel.getJobModel().getFailureAlertEmails().forEach(kweryMail::addTo);

        try {
            mailService.send(kweryMail);
            logger.info("Job id {} and execution id {} report generation failure alert email sent to {}",
                    jobModel.getId(), jobExecutionModel.getId(), String.join(", ", jobModel.getFailureAlertEmails()));
        } catch (Exception e) {
            logger.error("Exception while trying to send report generation failure alert email for job id {} and execution id {}",
                    jobModel.getId(), jobExecutionModel.getId(), e);
        }
    }
}
