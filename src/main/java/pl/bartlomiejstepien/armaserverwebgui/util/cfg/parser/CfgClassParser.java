package pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser;

import java.io.BufferedReader;

public interface CfgClassParser<T>
{
    T parse(BufferedReader bufferedReader);
}
