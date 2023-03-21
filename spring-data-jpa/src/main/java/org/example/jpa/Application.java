package org.example.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.config.Task;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * @author renc
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return new CommandLineRunner() {

            @Autowired
            TaskRepository taskRepository;

            @Autowired
            RecordRepository recordRepository;

            @Autowired
            EntityManager entityManager;

            @Override
            @Transactional
            public void run(String... args) throws Exception {
                // Task t = new Task(new TaskId(randomUUID().toString(), "renc"), new DatasetId("1"));
                // t.setDecisions(Arrays.asList(new Decision(t, "Test-1")));
                // taskRepository.save(t);
                //
                // List<Task> tasks = taskRepository.findAll();
                // System.out.println(tasks);

                Task t = taskRepository.getById(1L);
                System.out.println(t);
//                Task t = entityManager.getReference(Task.class, 1L);

                // RecordMaterial material = new RecordMaterial(
                //         randomUUID().toString(),
                //         "This is content!",
                //         "sender",
                //         "receiver",
                //         System.currentTimeMillis());
                // Record record = new Record(new Assignee(t.id()), material);
                // recordRepository.save(record);
                //
                // List<Record> records = recordRepository.findAll();
                // System.out.println(records);
            }
        };
    }
}