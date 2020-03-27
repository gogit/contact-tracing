package com.thinktag.user.controller;

import com.thinktag.user.model.User;
import com.thinktag.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@ControllerAdvice
@RestController
public class ContactTracingController {

    @Autowired
    UserService users;

    @PostMapping("api/register")
    User register(
            @NotNull @RequestParam("mobile") final String mobile)throws Exception {
        User user =  new User();
        user.setMobile(mobile);
        user.setValidationCode(UUID.randomUUID().toString());
        user.setValidationCodeHash(user.getHash(user.getValidationCode()));
        users.save(user);
        return user;
    }

    @PostMapping("api/contact")
    void contact(
            @NotNull @RequestParam("validationCode") final String validationCode,
            @NotNull @RequestParam("validationHash") final String hash) {

        users.saveAssociation(validationCode, hash);
    }

    @GetMapping("api/directContact")
    List<User> contact(
            @NotNull @RequestParam("validationCode") final String validationCode) {
        return users.findDirectContactsByValidationCode(validationCode);
    }

    @GetMapping("api/contactTrace")
    List<User> contactTrace(
            @NotNull @RequestParam("validationCode") final String validationCode) {
        return users.findContactsByValidationCode(validationCode);
    }
}