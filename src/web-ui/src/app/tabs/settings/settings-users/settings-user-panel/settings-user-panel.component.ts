import {Component, EventEmitter, inject, Input, OnInit, Output} from '@angular/core';
import {AswgUser} from "../../../../service/users.service";
import {FormGroup} from "@angular/forms";
import {EditUserFormService} from "./edit-user-form.service";
import {AswgAuthority} from "../../../../model/authority.model";
import {DialogService} from "../../../../service/dialog.service";
import {PasswordChangeModalComponent} from "./password-change-modal/password-change-modal.component";

@Component({
  selector: 'app-settings-user-panel',
  templateUrl: './settings-user-panel.component.html',
  styleUrl: './settings-user-panel.component.scss',
  standalone: false
})
export class SettingsUserPanelComponent implements OnInit {

  formService: EditUserFormService = inject(EditUserFormService);
  dialogService: DialogService = inject(DialogService);

  @Input({required: true})
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
    if(this.form.valid) {
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
    this.dialogService.open(PasswordChangeModalComponent,
      (dialogResult) => {
    }, this.user)
  }
}
