package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mod_preset")
@Entity
public class ModPresetEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Table(name = "mod_preset_entry")
    @Entity
    public static class EntryEntity
    {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "mod_name")
        private String name;

        @Column(name = "mod_preset_id")
        private Long modPresetId;

        @Column(name = "mod_id")
        private Long modId;
    }
}
