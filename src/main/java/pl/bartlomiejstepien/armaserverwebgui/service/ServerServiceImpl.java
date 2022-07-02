package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.model.Server;

import java.util.List;

@Service
@AllArgsConstructor
public class ServerServiceImpl implements ServerService {

    @Override
    public List<Server> getServers()
    {
        return null;
    }
}
