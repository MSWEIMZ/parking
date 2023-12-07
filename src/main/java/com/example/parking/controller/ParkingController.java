package com.example.parking.controller;

import com.example.parking.Service.ParkingService;
import com.example.parking.entity.Parking;
import com.example.parking.mapper.ParkingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/parking")
public class ParkingController {
    @Autowired
    private ParkingService parkingService;

    @GetMapping("/findAll")
    public List<Parking> query(){
        return parkingService.findAll();
    }

    @GetMapping("/findLatest1000")
    public List<Parking> findLatest1000(){
        return parkingService.findLatest1000();
    }

    @PostMapping("/insertData")
    public ResponseEntity<String> insertData(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("请选择文件上传");
        }

        try {
            parkingService.insertData(file);
            return ResponseEntity.ok("上传成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

