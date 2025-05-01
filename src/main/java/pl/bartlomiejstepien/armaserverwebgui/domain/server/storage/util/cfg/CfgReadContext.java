package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import java.io.BufferedReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CfgReadContext
{
    private StringBuilder readData;
    private BufferedReader bufferedReader;
    private Class<?> clazz;

    public String prepareErrorMessage()
    {
        return "'" + getCurrentlyReadData() + "<-- could not be parsed.'";
    }

    private String getCurrentlyReadData()
    {
        return this.readData.toString();
    }
}
