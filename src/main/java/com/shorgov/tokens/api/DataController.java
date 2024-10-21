package com.shorgov.tokens.api;

import com.shorgov.tokens.model.DataResponse;
import com.shorgov.tokens.model.DataInput;
import com.shorgov.tokens.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DataController {
    private final DataService dataService;

    @GetMapping("/data/free")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse getFreeData() {//leverage default @ResponseBody
        return new DataResponse("free data");
    }

    @GetMapping("/data/admin")
    public ResponseEntity<DataResponse> getAdminData() { //explicit ResponseEntity
        return ResponseEntity.ok(new DataResponse("Admin configuration - data"));
    }

    @GetMapping("/data/secured")
    @Secured({"ADMIN"})
    public ResponseEntity<DataResponse> getsecured() { //explicit ResponseEntity
        return ResponseEntity.ok(new DataResponse("Secured annotation - data"));
    }

    @GetMapping("/data")
    public ResponseEntity<List<DataResponse>> getData() {
        return ResponseEntity.ok(dataService.getAllData());
    }

    //usually location should be supplied but let;s return just string for the example
    @PostMapping("/data/add")
    @ResponseStatus(HttpStatus.CREATED)
    public String addData(@RequestBody DataInput data) {
        dataService.saveData(data.content());
        return "OK";
    }
}
