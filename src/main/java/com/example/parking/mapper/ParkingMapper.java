package com.example.parking.mapper;

import com.example.parking.entity.Parking;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ParkingMapper {
    @Select("select * from parking ")
    public List<Parking> find();

    @Select("select * from parking order by time desc limit 1000")
    public List<Parking> findLatest1000();


    @Insert("insert into parking (time, capacity) values (#{time}, #{capacity})")
    public int insert(Parking parking);
}
