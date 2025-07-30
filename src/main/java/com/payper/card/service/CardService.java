package com.payper.card.service;

import com.payper.card.dto.CardResponse;
import com.payper.card.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class CardService {
    private final CardMapper cardMapper;

    public List<CardResponse> getAllCards() {
        return cardMapper.selectAllCards();
    }
}
