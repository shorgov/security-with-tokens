package com.shorgov.tokens.service;

import com.shorgov.tokens.model.DataResponse;
import com.shorgov.tokens.repo.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataService {
    private final DataRepository dataRepo;

    public List<DataResponse> getAllData() {
        log.info("getAllData");
        return dataRepo.getAllData().stream()
                .map(DataResponse::new).toList();
    }

    public void saveData(String text) {
        log.info("saveData");
        dataRepo.addData(text);
    }
}
