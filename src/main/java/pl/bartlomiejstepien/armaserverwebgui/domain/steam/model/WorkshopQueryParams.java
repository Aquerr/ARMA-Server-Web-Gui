package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.Arrays;
import java.util.Optional;

@Value
@Builder
public class WorkshopQueryParams
{
    String cursor;
    String searchText;
    boolean searchByModId;
    SortingType sortingType;
    int daysPeriod;

    @Getter
    public enum SortingType
    {
        TEXT_RELEVANCE("TEXT_RELEVANCE"),
        POPULARITY("POPULARITY"),
        PUBLICATION_DATE("PUBLICATION_DATE"),
        LAST_UPDATED("LAST_UPDATED"),
        MOST_SUBSCRIBERS("MOST_SUBSCRIBERS");

        private final String code;

        SortingType(final String code)
        {
            this.code = code;
        }

        public static Optional<SortingType> findByCode(String code)
        {
            return Optional.ofNullable(code)
                    .flatMap(branchCode -> Arrays.stream(values())
                            .filter(sortingType -> sortingType.getCode().equals(branchCode.toLowerCase()))
                            .findFirst());
        }
    }
}
