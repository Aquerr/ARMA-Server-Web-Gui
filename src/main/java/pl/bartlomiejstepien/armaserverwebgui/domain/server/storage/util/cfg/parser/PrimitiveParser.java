package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

public class PrimitiveParser
{
    public static <T> T parse(String input, Class<T> clazz)
    {
        if (clazz.equals(Boolean.TYPE)) {
            return (T)Boolean.valueOf(input);
        }
        else if (clazz.equals(Integer.TYPE)) {
            return (T)Integer.valueOf(input);
        } else if (clazz.equals(Double.TYPE)) {
            return (T)Double.valueOf(input);
        } else if (clazz.equals(Float.TYPE)) {
            return (T)Float.valueOf(input);
        }

        throw new IllegalArgumentException("Unsupported primitive type = " + clazz);
    }
}
