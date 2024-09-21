package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

public class CfgValueHelper
{
    public static int toInt(boolean value)
    {
        return value ? 1 : 0;
    }
}
