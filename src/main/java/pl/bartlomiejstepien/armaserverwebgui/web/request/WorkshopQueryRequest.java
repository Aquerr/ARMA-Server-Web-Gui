package pl.bartlomiejstepien.armaserverwebgui.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkshopQueryRequest
{
    private String cursor;
    private String searchText;
}
