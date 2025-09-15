import { Component, inject, OnInit } from "@angular/core";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import { JobSettingsFormService } from "./job-settings-form.service";
import { MatHint, MatLabel, MatOption, MatSelect } from "@angular/material/select";
import { MatFormField } from "@angular/material/form-field";
import { JobSettingsService, UpdateJobSettingsRequest } from "../../../../service/job-settings.service";
import { MatButton } from "@angular/material/button";
import { MaskService } from "../../../../service/mask.service";
import { NotificationService } from "../../../../service/notification.service";
import { ActivatedRoute } from "@angular/router";
import { MatInput } from "@angular/material/input";
import { MatTooltip } from "@angular/material/tooltip";
import { MatIcon } from "@angular/material/icon";
import { DatePipe } from "@angular/common";

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
    MatHint
  ],
  templateUrl: "./job-view.component.html",
  styleUrl: "./job-view.component.scss"
})
export class JobViewComponent implements OnInit {
  public form!: FormGroup;

  public readonly formService: JobSettingsFormService = inject(JobSettingsFormService);

  private readonly jobSettingsService: JobSettingsService = inject(JobSettingsService);
  private readonly maskService: MaskService = inject(MaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  public jobName: string = "";

  ngOnInit(): void {
    this.jobName = this.activatedRoute.snapshot.params["name"];

    this.form = this.formService.getForm();

    this.maskService.show();
    this.jobSettingsService.getJobSettings(this.jobName).subscribe((response) => {
      this.formService.setForm(this.form, response);
      this.maskService.hide();
    });
  }

  get parameters() {
    return this.formService.getParametersControl(this.form);
  }

  save() {
    this.maskService.show();

    const parameters: { [key: string]: string } = {};
    this.form.value.parameters.forEach((parameter: any) => {
      parameters[parameter.name] = parameter.value;
    });

    const request = {
      enabled: this.form.value.enabled,
      cron: this.form.value.cron,
      parameters: parameters
    } as UpdateJobSettingsRequest;

    this.jobSettingsService.saveJobSettings(this.jobName, request).subscribe((response) => {
      this.formService.setForm(this.form, response);
      this.maskService.hide();
      this.notificationService.successNotification("Job settings have been updated.");
    });
  }
}
