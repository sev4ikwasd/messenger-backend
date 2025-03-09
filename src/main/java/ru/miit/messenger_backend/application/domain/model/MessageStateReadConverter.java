package ru.miit.messenger_backend.application.domain.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class MessageStateReadConverter implements Converter<Integer, MessageState> {
    @Override
    public MessageState convert(Integer source) {
        return MessageState.fromId(source);
    }
}
