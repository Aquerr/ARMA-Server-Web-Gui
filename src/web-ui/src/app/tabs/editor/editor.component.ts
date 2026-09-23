import {
  AfterViewInit,
  Component, computed,
  inject,
  OnInit,
  signal, viewChild,
  ViewEncapsulation
} from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { EditorFileType, EditorService } from "@service/editor.service";
import { MatButton } from "@angular/material/button";
import { NotificationService } from "@service/notification.service";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";
import { AswgSqfEditorComponent } from "@common-ui/aswg-sqf-editor/aswg-sqf-editor.component";
import { finalize } from "rxjs";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: "app-file-editor",
  imports: [
    MatButton,
    AswgSqfEditorComponent,
    MatIcon
  ],
  templateUrl: "./editor.component.html",
  styleUrl: "./editor.component.scss",
  encapsulation: ViewEncapsulation.None
})
export class EditorComponent implements OnInit, AfterViewInit {
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  private readonly editorService: EditorService = inject(EditorService);
  private readonly notificationService: NotificationService = inject(NotificationService);
  private readonly loadingSpinnerMaskService: LoadingSpinnerMaskService = inject(LoadingSpinnerMaskService);

  private fileType = signal<EditorFileType | null>(null);
  public fileNameFriendly = computed(() => EditorComponent.mapToFriendlyFileName(this.fileType()));

  codeEditorElement = viewChild.required<AswgSqfEditorComponent>("codeEditor");

  public ngOnInit() {
    this.fileType.set(this.activatedRoute.snapshot.paramMap.get("file") as EditorFileType);
  }

  public ngAfterViewInit() {
    this.fetchFileContent();
  }

  public save(): void {
    this.loadingSpinnerMaskService.show();
    this.editorService.saveFileContent(this.fileType()!, this.codeEditorElement().getCode())
      .pipe(finalize(() => this.loadingSpinnerMaskService.hide()))
      .subscribe(() => {
        this.notificationService.successNotification("File has been saved!");
      });
  }

  protected reload() {
    this.fetchFileContent();
  }

  private fetchFileContent() {
    this.loadingSpinnerMaskService.show();
    this.editorService.loadFileContent(this.fileType()!)
      .pipe(finalize(() => this.loadingSpinnerMaskService.hide()))
      .subscribe((response) => {
        this.codeEditorElement()?.setCode(response.content);
      });
  }

  private static mapToFriendlyFileName(fileType: EditorFileType | null) {
    return fileType == EditorFileType.SERVER_CONFIG ? "server.cfg" : "network.cfg (basic.cfg)";
  }
}
