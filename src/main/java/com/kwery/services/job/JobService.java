package com.kwery.services.job;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import it.sauronsoftware.cron4j.TaskExecutor;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class JobService {
    protected Logger logger = LoggerFactory.getLogger(JobService.class);

    protected final KweryScheduler kweryScheduler;
    protected final JobTaskFactory jobTaskFactory;
    protected final JobDao jobDao;

    //TODO - Needs to be done in a better way
    protected Map<Integer, String> jobIdSchedulerIdMap = new ConcurrentHashMap<>();

    @Inject
    public JobService(KweryScheduler kweryScheduler, JobTaskFactory jobTaskFactory, JobDao jobDao) {
        this.kweryScheduler = kweryScheduler;
        this.jobTaskFactory = jobTaskFactory;
        this.jobDao = jobDao;
    }

    public String schedule(int jobId) {
        logger.info("Scheduling job with id {}", jobId);
        JobTask jobTask = jobTaskFactory.create(jobId);
        JobModel jobModel = jobDao.getJobById(jobId);
        String schedulerId = kweryScheduler.schedule(jobModel.getCronExpression(), jobTask);
        jobIdSchedulerIdMap.put(jobId, schedulerId);
        return schedulerId;
    }

    @Start
    public void scheduleAllJobs() {
        List<JobModel> jobModels = jobDao.getAllJobs();
        for (JobModel jobModel : jobModels) {
            if (!"".equals(Strings.nullToEmpty(jobModel.getCronExpression()))) {
                schedule(jobModel.getId());
            }
        }
    }

    public TaskExecutor launch(int jobId) {
        logger.info("Launching job with id {}", jobId);
        return kweryScheduler.launch(jobTaskFactory.create(jobId));
    }

    public boolean stopExecution(String executionId) {
        logger.info("Trying to stop task execution with id {}", executionId);

        boolean found = false;

        for (TaskExecutor taskExecutor : kweryScheduler.getExecutingTasks()) {
            if (executionId.equals(taskExecutor.getGuid())) {
                taskExecutor.stop();
                found = true;
                logger.info("Task execution with id {} stopped successfully", executionId);
                break;
            }
        }

        if (!found) {
            logger.info("Task execution with id {} not found", executionId);
        }

        return found;
    }

    public void deschedule(int jobId) {
        logger.info("Deleting job with id {}", jobId);
        String id = jobIdSchedulerIdMap.get(jobId);

        if (id == null) {
            throw new RuntimeException("Schedule id not found for job id " + jobId);
        }

        jobIdSchedulerIdMap.remove(jobId);
        kweryScheduler.deschedule(id);
    }

    @VisibleForTesting
    public Map<Integer, String> getJobIdSchedulerIdMap() {
        return jobIdSchedulerIdMap;
    }
}
