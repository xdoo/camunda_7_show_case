package de.xapio.demo.api;

import de.xapio.demo.services.task.CompletedTaskService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.camunda.spin.Spin.JSON;

@Controller
@Slf4j
@RequestMapping(value = "/task")
public class TaskEndpoint {

    private final CompletedTaskService completedTaskService;

    public TaskEndpoint(CompletedTaskService completedTaskService) {
        this.completedTaskService = completedTaskService;
    }

    @PostMapping("/complete")
    public void getTaskCompleted(@RequestBody String msg) {
        log.info(msg);
        SpinJsonNode payload = JSON(msg);
        if(payload.hasProp("id")) {
            this.completedTaskService.completeTask(payload.prop("id").stringValue());
        }
    }

}
