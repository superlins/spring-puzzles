package org.example.ddd.infrastructure.persistence.hibernate;

import org.example.ddd.domain.model.record.Record;
import org.example.ddd.domain.model.record.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author renc
 */
@Repository
public class RecordRepositoryHibernate implements RecordRepository {

    @Autowired
    private EntityManager em;

    @Override
    public void save(Record record) {
        em.persist(record);
    }

    @Override
    public List<Record> findAll() {
        return em.createQuery("SELECT r FROM Record r").getResultList();
    }
}
