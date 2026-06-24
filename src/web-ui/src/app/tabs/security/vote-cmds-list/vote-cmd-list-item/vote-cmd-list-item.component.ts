import { Component, OnInit, ChangeDetectionStrategy, input, output } from "@angular/core";
import { MatCard, MatCardContent } from "@angular/material/card";
import { MatTooltip } from "@angular/material/tooltip";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { FormGroup, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatOption, MatSelect } from "@angular/material/select";
import { MatIcon } from "@angular/material/icon";
import { MatIconButton } from "@angular/material/button";
import { VoteCmdFormGroupWrapperControls } from "@app/tabs/security/security-form.service";

@Component({
  selector: "app-vote-cmd-list-item",
  templateUrl: "./vote-cmd-list-item.component.html",
  imports: [
    MatCard,
    MatCardContent,
    MatTooltip,
    MatFormField,
    MatLabel,
    MatInput,
    FormsModule,
    MatSelect,
    MatOption,
    MatIcon,
    MatIconButton,
    ReactiveFormsModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: "./vote-cmd-list-item.component.scss"
})
export class VoteCmdListItemComponent implements OnInit {
  public readonly voteCmdFormGroup = input.required<FormGroup<VoteCmdFormGroupWrapperControls>>();
  public readonly deleted = output<void>();

  ngOnInit() {
    if (this.voteCmdFormGroup().value.editing) {
      this.voteCmdFormGroup().controls.command.enable();
    } else {
      this.voteCmdFormGroup().controls.command.disable();
    }
  }

  deleteClick() {
    this.deleted.emit();
  }

  doubleClick() {
    this.voteCmdFormGroup().controls.editing.patchValue(!this.voteCmdFormGroup().controls.editing.value);

    if (this.voteCmdFormGroup().controls.editing.value) {
      this.voteCmdFormGroup().controls.command.enable();
    } else {
      this.voteCmdFormGroup().controls.command.disable();
    }
  }

  onEnter() {
    console.log(this.voteCmdFormGroup().value);
    if (this.voteCmdFormGroup().controls.editing.value) {
      this.doubleClick();
    }
  }
}
