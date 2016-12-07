package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.JobModel;
import ninja.jpa.UnitOfWork;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JobDao {
    protected Provider<EntityManager> entityManagerProvider;

    @Inject
    public JobDao(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Transactional
    public JobModel save(JobModel m) {
        EntityManager e = entityManagerProvider.get();

        if (m.getId() != null && m.getId() > 0) {
            return e.merge(m);
        } else {
            e.persist(m);
            return m;
        }
    }

    @UnitOfWork
    public JobModel getJobById(int jobId) {
        EntityManager e = entityManagerProvider.get();
        return e.find(JobModel.class, jobId);
    }

    @Transactional
    public JobModel getJobByLabel(String label) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<JobModel> cq = cb.createQuery(JobModel.class);
        Root<JobModel> root = cq.from(JobModel.class);
        cq.where(cb.equal(root.get("label"), label));
        TypedQuery<JobModel> tq = m.createQuery(cq);
        List<JobModel> jobModels = tq.getResultList();
        if (jobModels.isEmpty()) {
            return null;
        } else {
            if (jobModels.size() > 1) {
                throw new AssertionError(String.format("More than one job with label %s present", label));
            }
            return jobModels.get(0);
        }
    }

    public List<JobModel> getAllJobs() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<JobModel> cq = cb.createQuery(JobModel.class);
        Root<JobModel> rootEntry = cq.from(JobModel.class);
        CriteriaQuery<JobModel> all = cq.select(rootEntry);
        TypedQuery<JobModel> allQuery = e.createQuery(all);
        return allQuery.getResultList();
    }
}
