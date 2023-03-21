package com.oceanum.labeling.infrastructure.persistence.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;

import static org.springframework.data.jpa.repository.support.JpaEntityInformationSupport.getEntityInformation;
import static org.springframework.data.querydsl.SimpleEntityPathResolver.INSTANCE;

/**
 * @author renc
 */
public final class QueryCursorExecutor<T> extends QuerydslJpaPredicateExecutor<T> {

    private final Querydsl querydsl;

    private QueryCursorExecutor(Class<T> domain, EntityManager entityManager) {
        this(getEntityInformation(domain, entityManager), entityManager);
    }

    private QueryCursorExecutor(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager, INSTANCE, null);

        Path<T> path = INSTANCE.createPath(entityInformation.getJavaType());
        this.querydsl = new Querydsl(entityManager, new PathBuilder<T>(path.getType(), path.getMetadata()));
    }

    public static <T> QueryCursorExecutor instance(Class<T> domain, EntityManager entityManager) {
        return new QueryCursorExecutor(domain, entityManager);
    }

    /**
     * Returns a {@link Page} of entities {@link Expression} for row based transformation
     * matching the given {@link Predicate}. In case no match could be found, an empty {@link Page} is returned.
     *
     * @param expression must not be {@literal null}.
     * @param predicate must not be {@literal null}.
     * @param pageable may be {@link Pageable#unpaged()}, must not be {@literal null}.
     * @return a {@link Page} of entities matching the given {@link Predicate}.
     */
    public Page<T> findAll(Expression<T> expression, Predicate predicate, Pageable pageable) {

        Assert.notNull(expression, "Expression must not be null!");
        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");

        JPQLQuery<?> countQuery = createCountQuery(predicate);
        JPQLQuery<T> query = querydsl.applyPagination(pageable, createQuery(predicate).select(expression));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchCount);
    }
}