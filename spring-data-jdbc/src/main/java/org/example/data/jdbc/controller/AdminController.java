package org.example.data.jdbc.controller;

import org.example.data.jdbc.domain.Actor;
import org.example.data.jdbc.service.ActorService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author renc
 */
@RestController
public class AdminController {

    private final ActorService actorService;

    public AdminController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping("/orders")
    public Object orders(Actor actor, @PageableDefault Pageable pageable) {
        return actorService.actors(actor, pageable);
    }
}
