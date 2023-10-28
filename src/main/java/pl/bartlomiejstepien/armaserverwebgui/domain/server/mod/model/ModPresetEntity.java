package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("mod_preset")
public class ModPresetEntity
{
    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Table("mod_preset_entry")
    public static class EntryEntity
    {
        @Id
        @Column("id")
        private Long id;

        @Column("mod_name")
        private String name;

        @Column("mod_preset_id")
        private Long modPresetId;

        @Column("mod_id")
        private Long modId;
    }
}
