package pl.bartlomiejstepien.armaserverwebgui.web.request;

import lombok.Data;

import java.util.List;

@Data
public class SaveGeneralProperties
{
    private String hostname;
    private String serverDirectory;
    private String modsDirectory;
    private int port;
    private int maxPlayers;
    private List<String> motd;
    private int motdInterval;
    private boolean persistent;
    private boolean drawingInMap;
    private List<String> headlessClients;
    private List<String> localClients;
}
