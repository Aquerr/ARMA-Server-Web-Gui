package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModsView
{
    private List<ModView> disabledMods;
    private List<ModView> enabledMods;
    private List<ModView> notManagedMods;
}
