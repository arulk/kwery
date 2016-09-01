package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.QueryRun;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class QueryRunDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(QueryRun q) {
        EntityManager m = entityManagerProvider.get();
        m.persist(q);
        m.flush();
    }

    public QueryRun getByLabel(String label) {
        EntityManager m = entityManagerProvider.get();

        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<QueryRun> cq = cb.createQuery(QueryRun.class);

        Root<QueryRun> root = cq.from(QueryRun.class);
        cq.where(cb.equal(root.get("label"), label));

        TypedQuery<QueryRun> tq = m.createQuery(cq);

        List<QueryRun> runs = tq.getResultList();

        if (runs.isEmpty()) {
            return null;
        } else {
            if (runs.size() > 1) {
                throw new AssertionError(String.format("More than one query run present with label %s", label));
            }
            return runs.get(0);
        }
    }
}
