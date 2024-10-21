package com.shorgov.tokens.repo;

import com.shorgov.tokens.model.Role;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RolesRepository {

    private static final List<Role> roles = new ArrayList<>();

    public Optional<Role> findByName(String name){
        return roles.stream().filter(r->r.name().equals(name)).findFirst();
    }

    public void saveRole(Role role){
        int index = roles.indexOf(role);
        if(index != -1){
            roles.set(index, role);
        }else{
            roles.add(role);
        }
    }
}
