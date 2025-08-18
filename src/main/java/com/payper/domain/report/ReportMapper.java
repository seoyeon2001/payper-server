package com.payper.domain.report;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface ReportMapper {
    String findPayloadByUserAndMonth(@Param("userId") Integer userId,
                                     @Param("month") LocalDate month);

    Integer insert(@Param("userId") Integer userId,
               @Param("month") LocalDate month,
               @Param("payload") String payload);
}
