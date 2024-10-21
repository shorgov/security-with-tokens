package com.shorgov.tokens.repo;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class DataRepository {
    private static final List<String> data = new ArrayList<>();

    public List<String> getAllData(){
        return Collections.unmodifiableList(data);
    }

    public void addData(String text){
        data.add(text);
    }
}
