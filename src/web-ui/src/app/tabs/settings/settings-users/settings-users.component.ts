import { Component, inject, OnInit, signal, WritableSignal } from "@angular/core";
import { AswgUser, UsersService } from "../../../service/users.service";
import { MaskService } from "../../../service/mask.service";
import { NotificationService } from "../../../service/notification.service";
import { DialogService } from "../../../service/dialog.service";

@Component({
  selector: "app-settings-users",
  templateUrl: "./settings-users.component.html",
  styleUrl: "./settings-users.component.scss",
  standalone: false
})
export class SettingsUsersComponent implements OnInit {
  private readonly maskService: MaskService = inject(MaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);
  private readonly dialogService: DialogService = inject(DialogService);
  private readonly usersService: UsersService = inject(UsersService);

  users: WritableSignal<AswgUser[]> = signal([]);

  ngOnInit(): void {
    this.reloadUsersList();
  }

  delete(id: number | null) {
    if (!id) {
      this.reloadUsersList();
      return;
    }

    const onCloseCallback = (result: boolean) => {
      if (!result) return;

      this.maskService.show();
      this.usersService.deleteUser(id).subscribe((response) => {
        this.maskService.hide();
        this.reloadUsersList();
        this.notificationService.successNotification("User has been deleted");
      });
    };

    this.dialogService.openCommonConfirmationDialog(
      { question: "Are you sure you want to delete this user?" },
      onCloseCallback
    );
  }

  addUser() {
    const aswgUser = {
      id: null,
      username: "",
      password: "",
      locked: false,
      authorities: []
    } as AswgUser;
    this.users.update((value) => [...value, aswgUser]);
  }

  save(aswgUser: AswgUser) {
    this.maskService.show();
    if (aswgUser.id) {
      this.usersService.updateUser(aswgUser).subscribe((response) => {
        this.maskService.hide();
        this.reloadUsersList();
        this.notificationService.successNotification("User has been updated");
      });
    } else {
      this.usersService.addNewUser(aswgUser).subscribe((response) => {
        this.maskService.hide();
        this.reloadUsersList();
        this.notificationService.successNotification("User has been added");
      });
    }
  }

  private reloadUsersList() {
    this.maskService.show();
    this.usersService.getUsers().subscribe((users) => {
      this.users.set(users);
      this.maskService.hide();
    });
  }
}
