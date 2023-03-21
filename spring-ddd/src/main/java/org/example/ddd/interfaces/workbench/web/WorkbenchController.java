package org.example.ddd.interfaces.workbench.web;

import org.example.ddd.interfaces.workbench.facade.WorkbenchServiceFacade;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author renc
 */
@RestController
public class WorkbenchController {

    private final WorkbenchServiceFacade workbenchServiceFacade;

    public WorkbenchController(WorkbenchServiceFacade workbenchServiceFacade) {
        this.workbenchServiceFacade = workbenchServiceFacade;
    }
}
