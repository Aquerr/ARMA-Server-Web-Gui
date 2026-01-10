import { ChangeDetectorRef, Component, EventEmitter, inject, Input, OnInit, Output } from "@angular/core";
import { CommandListItem } from "./vote-cmd-list-item.model";
import { MatCard, MatCardContent } from "@angular/material/card";
import { MatTooltip } from "@angular/material/tooltip";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatOption, MatSelect } from "@angular/material/select";
import { MatIcon } from "@angular/material/icon";
import { MatIconButton } from "@angular/material/button";

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
  styleUrl: "./vote-cmd-list-item.component.scss"
})
export class VoteCmdListItemComponent implements OnInit {
  @Input() item!: CommandListItem;
  @Output() deleted: EventEmitter<void> = new EventEmitter<void>();

  private changeDetectorRef = inject(ChangeDetectorRef);

  form: FormGroup<{
    command: FormGroup<{
      name: FormControl<string>;
      allowedPreMission: FormControl<boolean>;
      allowedPostMission: FormControl<boolean>;
      votingThreshold: FormControl<number>;
      percentageSideVotingThreshold: FormControl<number>;
    }>;
    editing: FormControl<boolean>;
  }>;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.nonNullable.group({
      command: this.fb.nonNullable.group({
        name: this.fb.nonNullable.control("undefined"),
        allowedPreMission: this.fb.nonNullable.control<boolean>(false),
        allowedPostMission: this.fb.nonNullable.control<boolean>(false),
        votingThreshold: this.fb.nonNullable.control<number>(0),
        percentageSideVotingThreshold: this.fb.nonNullable.control<number>(0)
      }),
      editing: this.fb.nonNullable.control<boolean>(false)
    });
  }

  ngOnInit() {
    this.form.patchValue(this.item);
    if (this.item.editing) {
      this.form.controls.command.enable();
    } else {
      this.form.controls.command.disable();
    }
  }

  deleteClick() {
    this.deleted.emit();
  }

  doubleClick() {
    this.form.controls.editing.patchValue(!this.form.controls.editing.value);

    if (this.form.controls.editing.value) {
      this.form.controls.command.enable();
    } else {
      this.form.controls.command.disable();
    }
  }

  onEnter() {
    if (this.form.controls.editing.value) {
      this.doubleClick();
    }
  }
}
