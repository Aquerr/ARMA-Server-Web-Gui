import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { JobSettingsService } from "../../../service/job-settings.service";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";

@Component({
  selector: "app-settings-jobs",
  imports: [FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: "./settings-jobs.component.html",
  styleUrl: "./settings-jobs.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SettingsJobsComponent implements OnInit {
  private readonly jobsSettingsService: JobSettingsService = inject(JobSettingsService);
  private readonly maskService: LoadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);

  public jobsNames: string[] = [];

  ngOnInit(): void {
    this.maskService.show();
    this.jobsSettingsService.getAllJobsNames().subscribe((response) => {
      this.jobsNames = response;
      this.maskService.hide();
      this.changeDetectorRef.markForCheck();
    });
  }
}
