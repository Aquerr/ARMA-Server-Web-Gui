package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.domain.editor.FileEditorService;
import pl.bartlomiejstepien.armaserverwebgui.web.request.FileContentSaveRequest;
import pl.bartlomiejstepien.armaserverwebgui.web.response.FileContentResponse;

@RestController
@RequestMapping("/api/v1/editor")
@RequiredArgsConstructor
public class EditorController
{
    private final FileEditorService fileEditorService;

    @GetMapping("/server-config")
    public ResponseEntity<FileContentResponse> getServerConfigContent()
    {
        return ResponseEntity.ok()
                .body(new FileContentResponse(this.fileEditorService.getServerConfigContent()));
    }

    @PostMapping("/server-config")
    public ResponseEntity<?> saveServerConfigContent(FileContentSaveRequest fileContentSaveRequest)
    {
        this.fileEditorService.saveServerConfigContent(fileContentSaveRequest.content());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/network-config")
    public ResponseEntity<?> getBasicNetworkConfigContent()
    {
        return ResponseEntity.ok()
                .body(new FileContentResponse(this.fileEditorService.getBasicNetworkFileContent()));
    }

    @PostMapping("/network-config")
    public ResponseEntity<?> saveBasicNetworkConfigContent(FileContentSaveRequest fileContentSaveRequest)
    {
        this.fileEditorService.saveBasicNetworkCongigContent(fileContentSaveRequest.content());
        return ResponseEntity.ok().build();
    }
}
