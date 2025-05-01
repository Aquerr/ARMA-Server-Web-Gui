package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public final class FileUtils
{
    public static boolean deleteFilesRecursively(final Path path, boolean followLinks)
    {
        if (path == null)
        {
            return false;
        }
        if (!Files.exists(path))
        {
            return false;
        }

        EnumSet<FileVisitOption> fileVisitOptions = followLinks ? EnumSet.of(FileVisitOption.FOLLOW_LINKS) : EnumSet.noneOf(FileVisitOption.class);

        try
        {
            Files.walkFileTree(path, fileVisitOptions, Integer.MAX_VALUE, new SimpleFileVisitor<>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    private FileUtils()
    {

    }
}
