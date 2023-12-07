package com.example.parking.controller;

import com.example.parking.Service.ForecastService;
import com.example.parking.entity.Parking;
import com.example.parking.entity.ParkingPrediction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/forecast")
public class ForecastController {

    @Autowired
    private ForecastService forecastService;

    @PostMapping
    public ResponseEntity<Resource> forecast() throws IOException {
        // 查询数据库获取数据
        List<Parking> parkingList = forecastService.getAllParkingData();

        // 创建CSV文件并写入数据
        File csvFile = forecastService.createCSVFile(parkingList);

        // 发送CSV文件到预测API
        byte[] result = forecastService.sendCSVToAPI(csvFile);

        // 解析API的响应结果并保存到数据库
        List<ParkingPrediction> predictionList = forecastService.parseResponse(result);
        forecastService.saveToDB(predictionList);

        // 将API的响应结果保存为文件
        File outputFile = forecastService.saveResponseToFile(result);

        // 创建一个Resource对象，用于返回文件作为响应
        Resource resource = forecastService.createResource(outputFile);

        // 返回响应，设置Content-Disposition头部以指定文件名
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + outputFile.getName() + "\"")
                .body(resource);
    }

    @GetMapping("/result")
    public ResponseEntity<List<ParkingPrediction>> getAllParkingPredictions() {
        List<ParkingPrediction> predictions = forecastService.getAllParkingPredictions();
        return ResponseEntity.ok(predictions);
    }
}





