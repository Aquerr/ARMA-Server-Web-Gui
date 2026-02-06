package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Converter
public class ListOfLongsConverter implements AttributeConverter<List<Long>, String>
{
    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(List<Long> longList)
    {
        return Optional.ofNullable(longList)
                .map(list -> list.stream()
                        .map(x -> Long.toString(x))
                        .collect(Collectors.joining(DELIMITER)))
                .orElse("");
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData)
    {
        return Optional.ofNullable(dbData)
                .filter(StringUtils::hasText)
                .map(data -> data.split(DELIMITER))
                .map(Arrays::asList)
                .map(list -> list.stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
