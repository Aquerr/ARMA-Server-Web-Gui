import { Component, ChangeDetectionStrategy, input, inject, OnInit, DestroyRef, ChangeDetectorRef } from "@angular/core";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { MatIconButton } from "@angular/material/button";
import { VoteCmdListItemComponent } from "./vote-cmd-list-item/vote-cmd-list-item.component";
import { MatIcon } from "@angular/material/icon";
import { VoteCmdFormGroupWrapperControls } from "@app/tabs/security/security-form.service";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";

@Component({
  selector: "app-vote-cmds-list",
  templateUrl: "./vote-cmds-list.component.html",
  imports: [
    MatIconButton,
    VoteCmdListItemComponent,
    MatIcon
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: "./vote-cmds-list.component.scss"
})
export class VoteCmdsListComponent implements OnInit {
  public readonly control = input.required<FormArray<FormGroup<VoteCmdFormGroupWrapperControls>>>();
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  public ngOnInit(): void {
    this.control().valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      this.changeDetectorRef.markForCheck();
    });
  }

  deleteItem(index: number) {
    this.control().removeAt(index);
  }

  addNewCommand() {
    const voteCmdFormGroup = this.fb.group<VoteCmdFormGroupWrapperControls>({
      command: this.fb.nonNullable.group({
        name: this.fb.nonNullable.control("undefined"),
        allowedPreMission: this.fb.nonNullable.control<boolean>(false),
        allowedPostMission: this.fb.nonNullable.control<boolean>(false),
        votingThreshold: this.fb.nonNullable.control<number>(0),
        percentageSideVotingThreshold: this.fb.nonNullable.control<number>(0)
      }),
      editing: this.fb.nonNullable.control(true)
    });

    this.control().push(voteCmdFormGroup);
  }
}
