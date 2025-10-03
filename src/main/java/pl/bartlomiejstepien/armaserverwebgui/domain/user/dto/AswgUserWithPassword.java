package pl.bartlomiejstepien.armaserverwebgui.domain.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class AswgUserWithPassword extends AswgUser
{
    private String password;
}
