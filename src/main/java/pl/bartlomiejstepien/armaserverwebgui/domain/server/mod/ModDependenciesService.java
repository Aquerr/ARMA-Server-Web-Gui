package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.RelatedMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModDependenciesService
{
    private final InstalledModRepository installedModRepository;
    private final SteamService steamService;

    @Transactional(readOnly = true)
    public List<RelatedMod> getDependants(long workshopFileId)
    {
        List<InstalledModEntity> installedModEntities = installedModRepository.findAll();
        List<Long> modDependantsIds = getDependantsIds(workshopFileId, installedModEntities);

        return modDependantsIds.stream()
                .map(id -> getDependantsRecursively(id, new ArrayList<>(), installedModEntities))
                .flatMap(Collection::stream)
                .toList();
    }

    private List<Long> getDependantsIds(long workshopFileId, List<InstalledModEntity> modEntities)
    {
        return modEntities.stream()
                .filter(entity -> entity.getDependenciesIds().contains(workshopFileId))
                .map(InstalledModEntity::getWorkshopFileId)
                .toList();
    }

    private List<RelatedMod> getDependantsRecursively(long workshopFileId, List<RelatedMod> dependencies, List<InstalledModEntity> modEntities)
    {
        if (dependencies.stream().anyMatch(dependency -> dependency.getWorkshopFileId() == workshopFileId))
            return dependencies;

        modEntities.stream()
                .filter(mod -> mod.getWorkshopFileId() == workshopFileId)
                .findFirst()
                .ifPresent(
                        modEntity -> dependencies.add(RelatedMod.dependant(modEntity.getWorkshopFileId(), modEntity.getName(), RelatedMod.Status.INSTALLED))
                );

        return dependencies;
    }

    @Transactional(readOnly = true)
    public List<RelatedMod> getDependencies(long workshopFileId)
    {
        List<InstalledModEntity> installedModEntities = installedModRepository.findAll();
        return getModDependenciesRecursively(workshopFileId, new ArrayList<>(), installedModEntities);
    }

    private List<RelatedMod> getModDependenciesRecursively(long workshopFileId, List<RelatedMod> dependencies, List<InstalledModEntity> installedModEntities)
    {
        List<Long> dependenciesIds = new LinkedList<>();
        InstalledModEntity modEntity = installedModEntities.stream().filter(mod -> mod.getWorkshopFileId() == workshopFileId)
                .findFirst()
                .orElse(null);

        if (modEntity != null)
        {
            dependenciesIds.addAll(modEntity.getDependenciesIds());
        }
        else
        {
            Optional.ofNullable(steamService.getWorkshopMod(workshopFileId))
                    .ifPresent(workshopMod -> dependenciesIds.addAll(workshopMod.getDependencies()));
        }


        if (dependenciesIds.isEmpty())
            return dependencies;

        for (long dependencyId : dependenciesIds)
        {
            InstalledModEntity installedDependencyModEntity = installedModEntities.stream().filter(mod -> mod.getWorkshopFileId() == dependencyId)
                    .findFirst()
                    .orElse(null);

            if (dependencies.stream().anyMatch(dependency -> dependency.getWorkshopFileId() == dependencyId))
                continue;

            if (installedDependencyModEntity != null)
            {
                dependencies.add(RelatedMod.dependency(dependencyId, installedDependencyModEntity.getName(), RelatedMod.Status.INSTALLED));
                if (!installedDependencyModEntity.getDependenciesIds().isEmpty())
                    getModDependenciesRecursively(dependencyId, dependencies, installedModEntities);
            }
            else
            {
                WorkshopMod workshopMod = steamService.getWorkshopMod(dependencyId);
                String title = Optional.ofNullable(workshopMod).map(WorkshopMod::getTitle).orElse("Unknown");
                dependencies.add(RelatedMod.dependency(dependencyId, title, RelatedMod.Status.NOT_INSTALLED));

                Optional.ofNullable(workshopMod).map(WorkshopMod::getDependencies)
                        .ifPresent(deps ->
                        {
                            if (!deps.isEmpty())
                            {
                                getModDependenciesRecursively(dependencyId, dependencies, installedModEntities);
                            }
                        });
            }
        }

        return dependencies;
    }

//    @Transactional(readOnly = true)
//    public List<RelatedMod> getDependencies(long workshopFileId)
//    {
//        List<InstalledModEntity> installedModEntities = installedModRepository.findAll();
//
//        List<Long> modDependenciesIds = getModDependencyIds(workshopFileId, installedModEntities);
//
//        return modDependenciesIds.stream()
//                .map(id -> getModDependenciesRecursively(id, new ArrayList<>(), installedModEntities))
//                .flatMap(Collection::stream)
//                .toList();
//    }
//
//    private List<Long> getModDependencyIds(long workshopFileId, List<InstalledModEntity> modEntities) {
//        InstalledModEntity modEntity = modEntities.stream().filter(mod -> mod.getWorkshopFileId() == workshopFileId)
//                .findFirst()
//                .orElse(null);
//
//        if (modEntity != null)
//        {
//            return modEntity.getDependenciesIds();
//        }
//        else
//        {
//            return Optional.ofNullable(steamService.getWorkshopMod(workshopFileId))
//                    .map(WorkshopMod::getDependencies)
//                    .orElse(List.of());
//        }
//    }

//    private List<RelatedMod> getModDependenciesRecursively(long workshopFileId, List<RelatedMod> dependencies, List<InstalledModEntity> modEntities)
//    {
//        if (dependencies.stream()
//                .anyMatch(dependency -> dependency.getWorkshopFileId() == workshopFileId))
//            return dependencies;
//
//        InstalledModEntity modEntity = modEntities.stream().filter(mod -> mod.getWorkshopFileId() == workshopFileId)
//                .findFirst()
//                .orElse(null);
//
//        List<Long> dependencyIds = List.of();
//        if (modEntity != null)
//        {
//            dependencies.add(RelatedMod.dependency(workshopFileId, modEntity.getName(), RelatedMod.Status.INSTALLED));
//            dependencyIds = modEntity.getDependenciesIds();
//        }
//        else
//        {
//            // get from workshop
//            WorkshopMod workshopMod = steamService.getWorkshopMod(workshopFileId);
//            if (workshopMod != null)
//            {
//                dependencies.add(RelatedMod.dependency(workshopFileId, workshopMod.getTitle(), RelatedMod.Status.NOT_INSTALLED));
//                dependencyIds = workshopMod.getDependencies();
//            }
//            else
//            {
//                dependencies.add(RelatedMod.dependency(workshopFileId, "Unknown", RelatedMod.Status.NOT_INSTALLED));
//            }
//        }
//
//        List<RelatedMod> modDependencies = dependencyIds.stream()
//                .map(id -> getModDependenciesRecursively(id, dependencies, modEntities))
//                .flatMap(Collection::stream)
//                .toList();
//        dependencies.addAll(modDependencies);
//        return dependencies;
//    }
}
