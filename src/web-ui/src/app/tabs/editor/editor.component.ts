import {
  AfterViewInit,
  Component, computed,
  ElementRef,
  inject,
  OnInit,
  signal,
  ViewChild,
  ViewEncapsulation
} from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { EditorFileType, EditorService } from "@service/editor.service";
import { MatButton } from "@angular/material/button";
import { CodeJar } from "codejar";
import { withLineNumbers } from "codejar-linenumbers";
import { NotificationService } from "@service/notification.service";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";

@Component({
  selector: "app-file-editor",
  imports: [
    MatButton
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

  private jar!: CodeJar;

  @ViewChild("code") codeElement!: ElementRef<HTMLDivElement>;

  public ngOnInit() {
    this.fileType.set(this.activatedRoute.snapshot.paramMap.get("file") as EditorFileType);
  }

  public ngAfterViewInit() {
    this.loadingSpinnerMaskService.show();
    this.prepareCodeJar();
    this.editorService.loadFileContent(this.fileType()!).subscribe((response) => {
      this.jar.updateCode(response.content);
      this.loadingSpinnerMaskService.hide();
    });
  }

  public save(): void {
    console.log("EDITOR content: ", this.jar.toString());
    this.editorService.saveFileContent(this.fileType()!, this.jar.toString()).subscribe(() => {
      this.notificationService.successNotification("File has been saved!");
    });
  }

  private prepareCodeJar() {
    this.jar = CodeJar(this.codeElement.nativeElement, withLineNumbers(EditorComponent.highlightMethod), {
      tab: "    "
    });
  }

  static highlightMethod(editor: HTMLElement) {
    let code = editor.textContent;

    // Escape HTML entities to prevent rendering glitches
    code = code.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");

    // Apply regex rules sequentially (order matters!)
    code = code
      // Strings ("text" or 'text')
      .replace(/(["'])(?:(?=(\\?))\2.)*?\1/g, "<span class=\"sqf-string\">\$&</span>")
      // Single line and block comments
      .replace(/(\/\/.*|\/\*[\s\S]*?\*\/)/g, "<span class=\"sqf-comment\">\$&</span>")
      // Numbers
      .replace(/\b\d+(\.\d+)?\b/g, "<span class=\"sqf-number\">\$&</span>")
      // Core SQF Control structures / keywords
      .replace(/\b(if|then|else|while|do|for|from|to|step|forEach|switch|case|default|exitWith|try|catch|private)\b/g, "<span class=\"sqf-keyword\">\$&</span>")
      // Common SQF magical globals / commands
      .replace(/\b(player|params|hint|format|createVehicle|getPos|setPos|alive|isNull|isNil|this|_this|_x|_exception)\b/g, "<span class=\"sqf-command\">\$&</span>")
      // Local variables (prefixed with underscore)
      .replace(/\b_[a-zA-Z0-9_]+\b/g, "<span class=\"sqf-variable\">\$&</span>");

    editor.innerHTML = code;
  }

  private static mapToFriendlyFileName(fileType: EditorFileType | null) {
    return fileType == EditorFileType.SERVER_CONFIG ? "server.cfg" : "network.cfg (basic.cfg)";
  }
}
