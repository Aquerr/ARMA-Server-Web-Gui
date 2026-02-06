package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModsCollection
{
    private List<Mod> disabledMods;
    private List<Mod> enabledMods;
    private List<Mod> notManagedMods;
}
