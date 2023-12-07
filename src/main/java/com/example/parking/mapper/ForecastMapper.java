package com.example.parking.mapper;

import com.example.parking.entity.ParkingPrediction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ForecastMapper {

    //将预测结果插入数据库
    @Insert("INSERT INTO parking_prediction (ds, yhat, yhat_lower, yhat_upper) " +
            "VALUES (#{ds}, #{yhat}, #{yhatLower}, #{yhatUpper})")
    int insert(ParkingPrediction parkingPrediction);

    //将预测结果返回
    @Select("SELECT * FROM parking_prediction")
    List<ParkingPrediction> getAllPredictions();

}
