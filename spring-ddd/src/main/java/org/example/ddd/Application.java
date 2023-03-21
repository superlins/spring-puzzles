package org.example.ddd;

import org.example.ddd.domain.model.record.Record;
import org.example.ddd.domain.model.record.RecordId;
import org.example.ddd.domain.model.record.RecordRepository;
import org.example.ddd.domain.model.task.Task;
import org.example.ddd.domain.model.task.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author renc
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner runner(TaskRepository taskRepository, RecordRepository recordRepository) {

        return new CommandLineRunner() {

            @Override
            @Transactional
            public void run(String... args) throws Exception {
//                Task t = new Task(new TaskId("test1", "admin"), new DatasetId("1"));
//                t.setTbSpace(TBSpace.newTBSpace());
//                taskRepository.save(t);
                List<Task> tasks = taskRepository.findAll();

                Record record = new Record(new RecordId("1"), "This is content!", "sender", "receiver", System.currentTimeMillis());
                record.setLabels("tag1,tag2");
                recordRepository.save(record);
                List<Record> records = recordRepository.findAll();

                System.out.println();
            }
        };
    }
}