package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModsView
{
    private List<ModView> disabledMods;
    private List<ModView> enabledMods;
    private List<ModView> notManagedMods;
}
