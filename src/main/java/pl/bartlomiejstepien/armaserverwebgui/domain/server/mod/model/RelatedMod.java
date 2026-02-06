package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import lombok.Value;

@Value
public class RelatedMod
{
    Long workshopFileId;
    String name;
    Status status;
    RelationType relationType;

    public static RelatedMod dependency(long workshopFileId, String name, Status status)
    {
        return new RelatedMod(workshopFileId, name, status, RelationType.DEPENDENCY);
    }

    public static RelatedMod dependant(long workshopFileId, String name, Status status)
    {
        return new RelatedMod(workshopFileId, name, status, RelationType.DEPENDANT);
    }

    public enum Status
    {
        INSTALLED,
        NOT_INSTALLED
    }

    public enum RelationType
    {
        DEPENDENCY,
        DEPENDANT
    }
}
