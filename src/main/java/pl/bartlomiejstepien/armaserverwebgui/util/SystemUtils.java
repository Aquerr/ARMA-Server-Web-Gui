package pl.bartlomiejstepien.armaserverwebgui.util;

public class SystemUtils
{
    public static boolean isWindows()
    {
        final String osName = System.getProperty("os.name");
        return osName.startsWith("Win") || osName.startsWith("win");
    }
}
