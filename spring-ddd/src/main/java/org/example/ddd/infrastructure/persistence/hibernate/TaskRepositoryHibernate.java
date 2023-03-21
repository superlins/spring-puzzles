package org.example.ddd.infrastructure.persistence.hibernate;

import org.example.ddd.domain.model.task.Task;
import org.example.ddd.domain.model.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author renc
 */
@Repository
public class TaskRepositoryHibernate implements TaskRepository {

    @Autowired
    private EntityManager em;

    @Override
    public void save(Task t) {
        em.persist(t);
    }

    @Override
    public List<Task> findAll() {
        return em.createQuery("SELECT t FROM Task t").getResultList();
    }
}
