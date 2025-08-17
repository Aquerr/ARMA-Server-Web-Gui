package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum SteamArmaBranch
{
    PUBLIC("public"),
    PROFILING("profiling"),
    CONTACT("contact"),
    CREATOR_DLC("creatordlc");

    private final String code;

    SteamArmaBranch(String code)
    {
        this.code = code;
    }
    
    public static Optional<SteamArmaBranch> findByCode(String code)
    {
        return Optional.ofNullable(code)
                .flatMap(branchCode -> Arrays.stream(values())
                        .filter(branch -> branch.getCode().equals(branchCode.toLowerCase()))
                        .findFirst());
    }
}
