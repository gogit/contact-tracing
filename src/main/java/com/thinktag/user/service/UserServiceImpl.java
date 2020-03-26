package com.thinktag.user.service;

import com.thinktag.user.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class UserServiceImpl implements UserService {

    Map<String, User> users = new HashMap<>();

    @Override
    public User save(final User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public Optional<User> find(final String id) {
        return ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByMobile(final String mobile) {
        return users
                .values()
                .stream()
                .filter(u -> Objects.equals(mobile, u.getMobile()))
                .findFirst();
    }

    @Override
    public Optional<User> findByValidationCode(String validationCode) {
        return users
                .values()
                .stream()
                .filter(u -> Objects.equals(validationCode, u.getValidationCode()))
                .findFirst();
    }

    @Override
    public Optional<User> findByValidationCodeHash(String validationCodeHash) {
        return users
                .values()
                .stream()
                .filter(u -> Objects.equals(validationCodeHash, u.getValidationCodeHash()))
                .findFirst();
    }

    @Override
    public void saveAssociation(String validationCode, String... validationCodeHash) {
        Optional<User> user = findByValidationCode(validationCode);
        if(user.isPresent()){
            for(String vc: validationCodeHash){
                Optional<User> usern = findByValidationCodeHash(vc);
                if(usern.isPresent()) {
                    user.get().associate(usern.get());
                }
            }
        }
    }

    @Override
    public List<User> findDirectContactsByValidationCode(String validationCode) {
        Optional<User> user = findByValidationCode(validationCode);
        if (user.isPresent()) {
            return user.get().getUsers();
        }
        return Collections.emptyList();
    }

    @Override
    public List<User> findContactsByValidationCode(String validationCode) {
        Map<String, User> userMap = new HashMap<>();
        Optional<User> user = findByValidationCode(validationCode);
        if (user.isPresent()) {
            for(User u: user.get().getUsers()){
                recurse(u, userMap);
            }
        }
        return new ArrayList<>(userMap.values());
    }

    void recurse(User user, Map<String, User> map){
        if(map.containsKey(user.getMobile())){
            return;
        }
        map.put(user.getMobile(), user);
        for(User u: user.getUsers()){
            recurse(u, map);
        }
    }
}
