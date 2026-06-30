import { Component, input, output } from "@angular/core";
import { MatFormField, MatInput, MatLabel, MatPrefix } from "@angular/material/input";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: "app-aswg-search-box",
  imports: [
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatPrefix,
    MatInput,
    MatIcon
  ],
  templateUrl: "./aswg-search-box.component.html",
  styleUrl: "./aswg-search-box.component.scss"
})
export class AswgSearchBoxComponent {
  public control = input<FormControl>(new FormControl());
  public prefix = input<string>();
  public labelText = input<string>("");
  public placeholderText = input<string>("");

  public keyDownEvent = output<KeyboardEvent>();

  protected onKeyDown($event: KeyboardEvent): void {
    this.keyDownEvent.emit($event);
  }
}
