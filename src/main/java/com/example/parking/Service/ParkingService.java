package com.example.parking.Service;

import com.example.parking.entity.Parking;
import com.example.parking.mapper.ParkingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ParkingService {
    @Autowired
    private ParkingMapper parkingMapper;

    public List<Parking> findAll() {
        return parkingMapper.find();
    }

    public List<Parking> findLatest1000() {
        return parkingMapper.findLatest1000();
    }

    public void insertData(MultipartFile file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                if (lineCount == 0) { // 跳过第一行数据
                    lineCount++;
                    continue;
                }
                String[] columns = line.split(",");
                Date time = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(columns[0]); // 获取ds列的值
                int capacity = Integer.parseInt(columns[1]); // 获取dy列的值

                Parking parking = new Parking();
                parking.setTime(time);
                parking.setCapacity(capacity);

                parkingMapper.insert(parking); // 插入记录到数据库
            }
        }
    }
}

