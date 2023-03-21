package org.example.jpa.domain.model.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.scheduling.config.Task;

/**
 * @author renc
 */
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor {

    // default Specification<Task> spec(String name, Boolean running) {
    //     return  (root, query, builder) -> {
    //         List<Predicate> predicates = new ArrayList<>();
    //
    //         if (StringUtils.hasText(name)) {
    //             predicates.add(builder.like(root.get(Task_.TBIC).get(TBIC_.NAME), "%" + name + "%"));
    //         }
    //
    //         if (Objects.nonNull(running)) {
    //             Expression<TaskState> exp = root.get(Task_.STATE).as(TaskState.class);
    //             if (running) {
    //                 predicates.add(exp.in(Arrays.asList(LOADING, RUNNABLE, RUNNING)));
    //             } else {
    //                 predicates.add(exp.in(Arrays.asList(PAUSED, CANCELED, COMPLETED)));
    //             }
    //         }
    //
    //         return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
    //     };
    // }
}
