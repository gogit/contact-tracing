package com.thinktag.user.service;

import com.thinktag.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    Optional<User> find(String id);

    Optional<User> findByMobile(String mobile);

    Optional<User> findByValidationCode(String validationCode);

    Optional<User> findByValidationCodeHash(String validationCodeHash);

    void saveAssociation(String validationCode, String ...validationCodeHash);

    List<User> findContactsByValidationCode(String validationCode);

    List<User> findDirectContactsByValidationCode(String validationCode);

}
