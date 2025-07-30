package com.payper.card.mapper;

import com.payper.card.dto.CardResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CardMapper {
    List<CardResponse> selectAllCards();
}
