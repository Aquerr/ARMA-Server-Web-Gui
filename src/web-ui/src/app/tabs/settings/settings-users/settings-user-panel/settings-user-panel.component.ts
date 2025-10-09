import { Component, EventEmitter, inject, Input, OnInit, Output } from "@angular/core";
import { AswgUser } from "../../../../service/users.service";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import { EditUserFormService } from "./edit-user-form.service";
import { AswgAuthority } from "../../../../model/authority.model";
import { DialogService } from "../../../../service/dialog.service";
import { PasswordChangeModalComponent } from "./password-change-modal/password-change-modal.component";
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatIcon } from "@angular/material/icon";
import { MatOption, MatSelect } from "@angular/material/select";
import { AswgChipFormInputComponent } from "../../../../common-ui/aswg-chip-form-input/aswg-chip-form-input.component";
import { MatInput } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { DatePipe } from "@angular/common";

@Component({
  selector: "app-settings-user-panel",
  templateUrl: "./settings-user-panel.component.html",
  styleUrl: "./settings-user-panel.component.scss",
  standalone: true,
  imports: [
    MatLabel,
    ReactiveFormsModule,
    MatExpansionPanelTitle,
    MatExpansionPanelHeader,
    MatExpansionPanel,
    MatAccordion,
    MatIcon,
    MatSelect,
    MatOption,
    AswgChipFormInputComponent,
    MatFormField,
    MatInput,
    MatButtonModule,
    DatePipe
  ]
})
export class SettingsUserPanelComponent implements OnInit {
  formService: EditUserFormService = inject(EditUserFormService);
  dialogService: DialogService = inject(DialogService);

  @Input({ required: true })
  user!: AswgUser;

  @Output()
  deleted = new EventEmitter<number | null>();

  @Output()
  saved = new EventEmitter<AswgUser>();

  public form!: FormGroup;

  constructor() {
    this.form = this.formService.getForm();
  }

  ngOnInit(): void {
    this.formService.setForm(this.form, this.user);
  }

  save(): void {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.saved.emit(this.formService.asAswgUser(this.form));
    }
  }

  delete() {
    this.deleted.emit(this.user.id);
  }

  prepareAuthorities() {
    return Object.values(AswgAuthority).sort();
  }

  showEditPasswordModal() {
    this.dialogService.open(PasswordChangeModalComponent, (dialogResult) => {}, this.user);
  }
}
