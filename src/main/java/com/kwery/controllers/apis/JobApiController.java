package com.kwery.controllers.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.JobDao;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dtos.*;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobExecutionSearchFilter;
import com.kwery.services.job.JobService;
import com.kwery.views.ActionResult;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.views.ActionResult.Status.success;
import static java.util.stream.Collectors.toSet;
import static ninja.Results.json;

public class JobApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DISPLAY_DATE_FORMAT = "EEE MMM dd yyyy HH:mm";

    protected final DatasourceDao datasourceDao;
    protected final JobDao jobDao;
    protected final JobService jobService;
    protected final JobExecutionDao jobExecutionDao;

    @Inject
    public JobApiController(DatasourceDao datasourceDao, JobDao jobDao, JobService jobService, JobExecutionDao jobExecutionDao) {
        this.datasourceDao = datasourceDao;
        this.jobDao = jobDao;
        this.jobService = jobService;
        this.jobExecutionDao = jobExecutionDao;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result saveJob(JobDto jobDto) {
        if (logger.isTraceEnabled()) logger.trace("<");

        JobModel jobModel = jobDao.save(jobDtoToJobModel(jobDto));
        jobService.schedule(jobModel.getId());

        if (logger.isTraceEnabled()) logger.trace(">");
        return json().render(new ActionResult(success, ""));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listAllJobs() {
        if (logger.isTraceEnabled()) logger.trace("<");
        List<JobModel> jobs = jobDao.getAllJobs();
        if (logger.isTraceEnabled()) logger.trace(">");
        return json().render(jobs);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listJobExecutions(@PathParam("jobId") int jobId) {
        if (logger.isTraceEnabled()) logger.trace("<");
        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setJobId(jobId);
        List<JobExecutionModel> executions = jobExecutionDao.filter(filter);

        List<JobExecutionDto> dtos = new ArrayList<>(executions.size());

        for (JobExecutionModel execution : executions) {
            dtos.add(jobExecutionModelToJobExecutionDto(execution));
        }

        if (logger.isTraceEnabled()) logger.trace(">");
        return json().render(dtos);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result jobExecutionResult(@PathParam("jobExecutionId") String jobExecutionId) throws IOException {
        if (logger.isTraceEnabled()) logger.trace("<");

        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setExecutionId(jobExecutionId);

        List<JobExecutionModel> jobExecutionModels = jobExecutionDao.filter(filter);

        if (jobExecutionModels.isEmpty() || jobExecutionModels.get(0).getSqlQueryExecutionModels().isEmpty()) {
            if (logger.isTraceEnabled()) logger.trace(">");
            return json().render(new ActionResult(ActionResult.Status.failure, "Report not found"));
        } else {
            List<SqlQueryExecutionResultDto> sqlQueryExecutionResultDtos = new LinkedList<>();
            if (!jobExecutionModels.isEmpty()) {
                for (SqlQueryExecutionModel sqlQueryExecutionModel : jobExecutionModels.get(0).getSqlQueryExecutionModels()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String result = sqlQueryExecutionModel.getResult();
                    List<List<?>> jsonResult = new LinkedList<>();
                    if (!Strings.nullToEmpty(result).trim().equals("")) {
                        jsonResult = objectMapper.readValue(
                                result,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, List.class)
                        );
                    }

                    sqlQueryExecutionResultDtos.add(new SqlQueryExecutionResultDto(sqlQueryExecutionModel.getSqlQuery().getLabel(), jsonResult));
                }
            }

            if (logger.isTraceEnabled()) logger.trace(">");
            return json().render(new JobExecutionResultDto(jobExecutionModels.get(0).getJobModel().getLabel(), sqlQueryExecutionResultDtos));
        }
    }

    @VisibleForTesting
    public JobModel jobDtoToJobModel(JobDto jobDto) {
        JobModel jobModel = new JobModel();

        if (jobDto.getId() == 0) {
            jobModel.setId(null);
        } else {
            jobModel.setId(jobDto.getId());
        }

        jobModel.setLabel(jobDto.getLabel());
        jobModel.setCronExpression(jobDto.getCronExpression());
        jobModel.setSqlQueries(jobDto.getSqlQueries().stream().map(this::sqlQueryDtoToSqlQueryModel).collect(toSet()));
        return jobModel;
    }

    @VisibleForTesting
    public SqlQueryModel sqlQueryDtoToSqlQueryModel(SqlQueryDto dto) {
        SqlQueryModel model = new SqlQueryModel();

        if (dto.getId() == 0) {
            model.setId(null);
        } else {
            model.setId(dto.getId());
        }

        model.setLabel(dto.getLabel());
        model.setQuery(dto.getQuery());
        model.setDatasource(datasourceDao.getById(dto.getDatasourceId()));

        return model;
    }

    @VisibleForTesting
    public JobExecutionDto jobExecutionModelToJobExecutionDto(JobExecutionModel model) {
        JobExecutionDto jobExecutionDto = new JobExecutionDto();
        jobExecutionDto.setStart(new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(model.getExecutionStart()));
        jobExecutionDto.setEnd(new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(model.getExecutionEnd()));
        jobExecutionDto.setStatus(model.getStatus().name());
        jobExecutionDto.setExecutionId(model.getExecutionId());
        return jobExecutionDto;
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<?>> jsonResult = objectMapper.readValue(
                "",
                objectMapper.getTypeFactory().constructCollectionType(List.class, List.class)
        );

        System.out.println(jsonResult);
    }
}
