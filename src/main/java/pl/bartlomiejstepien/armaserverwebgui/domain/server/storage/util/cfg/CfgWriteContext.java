package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import java.io.BufferedWriter;
import java.lang.reflect.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CfgWriteContext
{
    private BufferedWriter bufferedWriter;
    private Object instance;
    private Field currentField;
    private int indentation;

    public void incrementIndentation()
    {
        this.indentation++;
    }

    public void decrementIndentation()
    {
        this.indentation--;
    }

    public String indentation()
    {
        return "\t".repeat(Math.max(0, indentation));
    }
}
