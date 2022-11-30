package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util;

public class SystemUtils
{
    public static boolean isWindows()
    {
        return org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
    }
}
