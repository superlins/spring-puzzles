package org.example.ddd.interfaces.workbench.facade.dto;

import lombok.Data;

/**
 * @author renc
 */
public interface TaskDTO {

    @Data
    class TaskReq {
        private String name;
        private String creator;
    }
}
