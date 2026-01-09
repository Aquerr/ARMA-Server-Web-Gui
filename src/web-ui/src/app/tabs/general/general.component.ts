import { AfterViewInit, ChangeDetectorRef, Component, signal } from "@angular/core";
import { LoadingSpinnerMaskService } from "../../service/loading-spinner-mask.service";
import { ServerGeneralService } from "../../service/server-general.service";
import { NotificationService } from "../../service/notification.service";
import { MotdListComponent } from "./motd-list/motd-list.component";
import { UnsafeService } from "../../service/unsafe.service";
import { OverwriteCommandlineParamsModalComponent } from "./unsafe/overwrite-commandline-params-modal/overwrite-commandline-params-modal.component";
import { DialogService } from "../../service/dialog.service";
import { PermissionService } from "../../service/permission.service";
import { AswgAuthority } from "../../model/authority.model";
import { MatError, MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { FormGroup, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatTooltip } from "@angular/material/tooltip";
import { MatOption, MatSelect } from "@angular/material/select";
import { MatButton } from "@angular/material/button";
import { GeneralFormGroup, GeneralFormService } from "./general-form.service";
import { AswgChipFormInputComponent } from "../../common-ui/aswg-chip-form-input/aswg-chip-form-input.component";

@Component({
  selector: "app-general",
  templateUrl: "./general.component.html",
  styleUrls: ["./general.component.scss"],
  imports: [MatFormField, MatLabel, FormsModule, MatInput, MatTooltip, MatSelect, MatOption, MotdListComponent, MatButton, ReactiveFormsModule, AswgChipFormInputComponent, MatError]
})
export class GeneralComponent implements AfterViewInit {
  form: FormGroup<GeneralFormGroup>;

  commandLineParams = signal<string>("");

  constructor(
    private readonly permissionsService: PermissionService,
    private readonly maskService: LoadingSpinnerMaskService,
    private readonly serverGeneralService: ServerGeneralService,
    private readonly notificationService: NotificationService,
    private readonly unsafeService: UnsafeService,
    private readonly dialogService: DialogService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    private readonly generalFormService: GeneralFormService
  ) {
    this.form = this.generalFormService.createForm();
  }

  ngAfterViewInit() {
    this.maskService.show();
    this.reloadGeneralProperties();
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      return;
    }

    this.maskService.show();
    const generalProperties = this.generalFormService.asGeneralProperties(this.form.controls);
    this.serverGeneralService.saveGeneralProperties(generalProperties).subscribe(() => {
      this.maskService.hide();
      this.notificationService.successNotification(
        "General settings have been updated!",
        "Success"
      );
    });
  }

  private reloadGeneralProperties() {
    this.serverGeneralService.getGeneralProperties().subscribe((response) => {
      this.generalFormService.setForm(this.form.controls, response);
      this.commandLineParams.set(response.commandLineParams);
      this.maskService.hide();
      this.changeDetectorRef.markForCheck();
    });
  }

  public overrideCommandLineParams() {
    const onCloseCallback = (result: boolean) => {
      if (!result) return;

      const closeCallback = (result: string) => {
        if (result == "null") return;

        this.maskService.show();
        this.unsafeService.overwriteStartupParams(result).subscribe(() => {
          this.notificationService.successNotification("Updated commandline parameters");
          this.reloadGeneralProperties();
        });
      };
      this.dialogService.open(
        OverwriteCommandlineParamsModalComponent,
        closeCallback,
        this.commandLineParams(),
        {
          width: "550px"
        }
      );
    };
    this.dialogService.openCommonConfirmationDialog(
      {
        question:
          "This is an unsafe feature. It is advised to not edit the command line directly. Are you sure you want to continue?"
      },
      onCloseCallback
    );
  }

  public formatCommandLineParams(commandLineParams: string) {
    return commandLineParams.replace(/\s/g, "\n");
  }

  public hasOverwriteCommandLineParamsPermission() {
    return this.permissionsService.hasAllAuthorities(
      [AswgAuthority.UNSAFE_OVERWRITE_STARTUP_PARAMS],
      false
    );
  }
}
