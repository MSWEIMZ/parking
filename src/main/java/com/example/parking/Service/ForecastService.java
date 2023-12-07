package com.example.parking.Service;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.parking.entity.Parking;
import com.example.parking.entity.ParkingPrediction;
import com.example.parking.mapper.ForecastMapper;
import com.example.parking.mapper.ParkingMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ForecastService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DruidDataSource dataSource = new DruidDataSource();

    @Autowired
    private ParkingMapper parkingMapper;

    @Autowired
    private ForecastMapper forecastMapper;

    @Value("${api.url}")
    private String apiUrl;

    public List<Parking> getAllParkingData() {
        return parkingMapper.find();
    }

    public File createCSVFile(List<Parking> parkingList) throws IOException {
        // 创建CSV文件
        File csvFile = new File("data.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // 将数据库中的数据写入CSV文件
            for (Parking parking : parkingList) {
                Date ds = parking.getTime();
                int dy = parking.getCapacity();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                String formattedTime = sdf.format(ds);
                writer.write(formattedTime + "," + dy);
                writer.newLine();
            }
        }

        return csvFile;
    }

    public byte[] sendCSVToAPI(File csvFile) throws IOException {
        // 发送POST请求到API
        RestTemplate restTemplate = new RestTemplate();

        // 添加MultipartHttpMessageConverter
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource resource = new FileSystemResource(csvFile);
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
        request.add("file", resource);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(request, headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, byte[].class);
        return response.getBody();
    }

    public List<ParkingPrediction> parseResponse(byte[] result) throws IOException {
        // 解析API的响应结果
        List<ParkingPrediction> predictionList = new ArrayList<>();
        String[] lines = new String(result, StandardCharsets.UTF_8).split("\n");
        boolean firstLine = true; // 添加一个标志来判断是否是第一行
        for (String line : lines) {
            if (firstLine) {
                firstLine = false;
                continue; // 跳过第一行（标题行）
            }
            String[] fields = line.split("\t");
            if (fields.length == 4) {
                ParkingPrediction prediction = new ParkingPrediction();
                prediction.setDs(fields[0]);
                prediction.setYhat(Double.parseDouble(fields[1]));
                prediction.setYhatLower(Double.parseDouble(fields[2]));
                prediction.setYhatUpper(Double.parseDouble(fields[3]));
                predictionList.add(prediction);
            }
        }
        return predictionList;
    }


    public void saveToDB(List<ParkingPrediction> predictionList) {
        // 将预测结果保存到数据库
        for (ParkingPrediction prediction : predictionList) {
            forecastMapper.insert(prediction);
        }
    }

    public File saveResponseToFile(byte[] result) throws IOException {
        // 将API的响应结果保存为文件
        File outputFile = new File("output.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String[] lines = new String(result, StandardCharsets.UTF_8).split("\n");
            for (String line : lines) {
                String[] fields = line.split("\t");
                if (fields.length == 4) {
                    writer.write(String.join(",", fields) + "\n");
                }
            }
        }
        return outputFile;
    }

    public Resource createResource(File file) {
        // 创建一个Resource对象，用于返回文件作为响应
        return new FileSystemResource(file);
    }

    public List<ParkingPrediction> getAllParkingPredictions() {
        return forecastMapper.getAllPredictions();
    }

}

