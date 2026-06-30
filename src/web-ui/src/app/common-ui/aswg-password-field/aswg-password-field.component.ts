import { Component, computed, inject, input, OnInit, signal } from "@angular/core";
import { MatError, MatFormField, MatInput, MatLabel, MatSuffix } from "@angular/material/input";
import { ControlContainer, FormControl, ReactiveFormsModule } from "@angular/forms";
import { MatTooltip } from "@angular/material/tooltip";
import { MatIcon } from "@angular/material/icon";
import { MatIconButton } from "@angular/material/button";

@Component({
  selector: "app-aswg-password-field",
  templateUrl: "./aswg-password-field.component.html",
  imports: [
    MatFormField,
    MatTooltip,
    ReactiveFormsModule,
    MatIconButton,
    MatIcon,
    MatError,
    MatLabel,
    MatInput,
    MatSuffix
  ],
  styleUrls: ["./aswg-password-field.component.scss"],
  viewProviders: [
    {
      provide: ControlContainer,
      useFactory: () => inject(ControlContainer, { skipSelf: true })
    }
  ]
})
export class AswgPasswordFieldComponent implements OnInit {
  public readonly controlName = input.required<string>();
  public readonly tooltip = input<string>("");
  public readonly labelText = input<string>("");
  public readonly tooltipPosition = input<"left" | "right" | "above" | "below" | "before" | "after">("right");
  public readonly tooltipPositionComputed = computed(() => this.tooltipPosition());

  public control!: FormControl<unknown>;
  private controlContainer = inject(ControlContainer, { host: false });

  protected readonly hide = signal<boolean>(true);

  public ngOnInit(): void {
    this.control = this.controlContainer.control?.get(this.controlName()) as FormControl;
  }

  protected toggleVisibility(event: PointerEvent): void {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }
}
