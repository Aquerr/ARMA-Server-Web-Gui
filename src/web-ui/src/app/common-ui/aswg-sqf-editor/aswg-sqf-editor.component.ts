import { Component, effect, ElementRef, output, viewChild, ViewEncapsulation } from "@angular/core";
import { CodeJar } from "codejar";
import { withLineNumbers } from "codejar-linenumbers";

@Component({
  selector: "app-aswg-sqf-editor",
  templateUrl: "./aswg-sqf-editor.component.html",
  styleUrls: ["./aswg-sqf-editor.component.scss"],
  encapsulation: ViewEncapsulation.None
})
export class AswgSqfEditorComponent {
  public codeChanged = output<string>();

  codeEditorElement = viewChild.required<ElementRef<HTMLDivElement>>("code");

  private jar!: CodeJar;

  constructor() {
    effect(() => {
      if (!this.jar && this.codeEditorElement()) {
        this.jar = CodeJar(this.codeEditorElement().nativeElement, withLineNumbers(AswgSqfEditorComponent.highlightSqf), {
          tab: "    "
        });

        this.jar.onUpdate((code) => {
          this.codeChanged.emit(code);
        });
      }
    });
  }

  public setCode(code: string) {
    this.jar.updateCode(code);
  }

  public getCode(): string {
    return this.jar.toString();
  }

  private static highlightSqf(editor: HTMLElement) {
    let code = editor.textContent;

    code = code.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");

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
}
