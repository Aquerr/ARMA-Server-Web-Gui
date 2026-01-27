import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from "@angular/core";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import { JobSettingsFormService } from "./job-settings-form.service";
import { MatHint, MatLabel, MatOption, MatSelect } from "@angular/material/select";
import { MatFormField } from "@angular/material/form-field";
import { JobSettingsService, UpdateJobSettingsRequest } from "../../../../service/job-settings.service";
import { MatButton } from "@angular/material/button";
import { LoadingSpinnerMaskService } from "../../../../service/loading-spinner-mask.service";
import { NotificationService } from "../../../../service/notification.service";
import { ActivatedRoute } from "@angular/router";
import { MatInput } from "@angular/material/input";
import { MatTooltip } from "@angular/material/tooltip";
import { MatIcon } from "@angular/material/icon";
import { DatePipe, NgClass } from "@angular/common";
import { JobStatus } from "../../../../model/job-settings.model";
import { DialogService } from "../../../../service/dialog.service";

@Component({
  selector: "app-job-view",
  imports: [
    ReactiveFormsModule,
    MatSelect,
    MatOption,
    MatFormField,
    MatLabel,
    MatButton,
    MatInput,
    MatTooltip,
    MatIcon,
    DatePipe,
    MatHint,
    NgClass
  ],
  templateUrl: "./job-view.component.html",
  styleUrl: "./job-view.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class JobViewComponent implements OnInit {
  public form!: FormGroup;

  public readonly formService: JobSettingsFormService = inject(JobSettingsFormService);

  private readonly jobSettingsService: JobSettingsService = inject(JobSettingsService);
  private readonly maskService: LoadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  private readonly dialogService: DialogService = inject(DialogService);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);

  public jobName: string = "";

  ngOnInit(): void {
    this.jobName = this.activatedRoute.snapshot.params["name"] as string;

    this.form = this.formService.getForm();

    this.maskService.show();
    this.jobSettingsService.getJobSettings(this.jobName).subscribe((response) => {
      this.formService.setForm(this.form, response);
      this.maskService.hide();
      this.changeDetectorRef.markForCheck();
    });
  }

  get parameters() {
    return this.formService.getParametersControl(this.form);
  }

  save() {
    this.maskService.show();

    const parameters: Record<string, string> = {};
    this.formService.getParametersControl(this.form).value.forEach((parameter: { name: string; description: string; value: string }) => {
      parameters[parameter.name] = parameter.value;
    });

    const request = {
      enabled: this.formService.getEnabledControl(this.form).value,
      cron: this.formService.getCronControl(this.form).value,
      parameters: parameters
    } as UpdateJobSettingsRequest;

    this.jobSettingsService.saveJobSettings(this.jobName, request).subscribe((response) => {
      this.formService.setForm(this.form, response);
      this.maskService.hide();
      this.notificationService.successNotification("Job settings have been updated.");
    });
  }

  protected readonly JobStatus = JobStatus;

  runNow() {
    const onCloseCallback = (result: boolean) => {
      if (!result) return;
      this.maskService.show();
      this.jobSettingsService.runJobNow(this.jobName).subscribe(() => {
        this.maskService.hide();
        this.notificationService.successNotification("Job has been started!");
      });
    };
    this.dialogService.openCommonConfirmationDialog({
      question: `Are you sure you want to start <strong>${this.jobName}</strong> job now?`
    }, onCloseCallback);
  }
}
