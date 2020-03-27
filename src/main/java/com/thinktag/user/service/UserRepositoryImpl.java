package com.thinktag.user.service;

import com.thinktag.user.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class  UserRepositoryImpl {

    private final String RECURSIVE_QUERY = "WITH RECURSIVE CONTACT_PATH (ID, USER_ID) "+
            "AS ( SELECT ID, USER_ID FROM CONTACT WHERE ID = %userId% "+
            "UNION ALL "+
            "SELECT C.ID, C.USER_ID FROM CONTACT C INNER JOIN  CONTACT_PATH CPA ON  CPA.USER_ID = C.ID ) " +
            "SELECT U.ID, U.mobile, U.validation_Code, U.validation_Code_Hash FROM USER U INNER JOIN CONTACT_PATH CPA ON U.ID = CPA.USER_ID;";

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> findContactTrace(Long id){
        List<User> users = new ArrayList<>();
        String query = RECURSIVE_QUERY.replace("%userId%", id.toString());
        System.out.println(query);
        Query q2 = entityManager.createNativeQuery(query, User.class);

        users.addAll(q2.getResultList());
        return users;
    }
}
