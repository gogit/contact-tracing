package com.thinktag.user.service;

import com.thinktag.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByMobile(String mobile);

    List<User> findByValidationCode(String validationCode);

    List<User> findByValidationCodeHash(String validationCodeHash);

}
