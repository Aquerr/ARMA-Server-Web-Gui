package pl.bartlomiejstepien.armaserverwebgui.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.bartlomiejstepien.armaserverwebgui.web.model.CdlcApiModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCdlcListResponse
{
    private List<CdlcApiModel> cdlcs;
}
