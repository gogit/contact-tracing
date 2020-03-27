package com.thinktag.user.service;

import com.thinktag.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repository1;

    @Autowired
    UserRepositoryImpl repository2;

    @Override
    public User save(final User user) {
        return repository1.save(user);
    }
    @Override
    public Optional<User> findByMobile(final String mobile) {
        List<User> l = repository1.findByMobile(mobile);
        return l.isEmpty()?Optional.empty():Optional.of(l.get(0));
    }

    @Override
    public Optional<User> findByValidationCode(String validationCode) {
        List<User> l = repository1.findByValidationCode(validationCode);
        return l.isEmpty()?Optional.empty():Optional.of(l.get(0));
    }

    @Override
    public Optional<User> findByValidationCodeHash(String validationCodeHash) {
        List<User> l = repository1.findByValidationCodeHash(validationCodeHash);
        return l.isEmpty()?Optional.empty():Optional.of(l.get(0));
    }

    @Override
    public void saveAssociation(String validationCode, String... validationCodeHash) {
        Optional<User> ouser = findByValidationCode(validationCode);
        if(ouser.isPresent()){
            User user = ouser.get();
            for(String vc: validationCodeHash){
                Optional<User> usern = findByValidationCodeHash(vc);
                if(usern.isPresent()) {
                    user.associate(usern.get());
                }
            }
            repository1.save(user);
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
        Optional<User> user = findByValidationCode(validationCode);
        if (user.isPresent()) {
            return repository2.findContactTrace(user.get().getId());
        }
        return Collections.emptyList();
    }


    //@Override
    public List<User> findContactsByValidationCode1(String validationCode) {
        Map<String, User> userMap = new HashMap<>();
        Optional<User> user = findByValidationCode(validationCode);
        if (user.isPresent()) {
            for(User u: user.get().getUsers()){
                recurse(u, userMap);
            }
        }
        userMap.remove(user.get().getMobile());
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
