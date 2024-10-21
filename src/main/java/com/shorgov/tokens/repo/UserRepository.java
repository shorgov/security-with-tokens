package com.shorgov.tokens.repo;

import com.shorgov.tokens.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private static final List<User> users = new ArrayList<>();

    public Optional<User> findByEmail(String email){
        return users.stream().filter(u->u.email().equals(email)).findFirst();
    }

    public void saveUser(User user){
        int index = users.indexOf(user);
        if(index != -1){
            users.set(index, user);
        }else{
            users.add(user);
        }
    }
}
