import { Component, inject, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { JobSettingsService } from "../../../service/job-settings.service";

@Component({
  selector: "app-settings-jobs",
  imports: [FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: "./settings-jobs.component.html",
  styleUrl: "./settings-jobs.component.scss"
})
export class SettingsJobsComponent implements OnInit {

  private readonly jobsSettingsService: JobSettingsService = inject(JobSettingsService);

  public jobsNames: string[] = [];

  ngOnInit(): void {
    this.jobsSettingsService.getAllJobsNames().subscribe((response) => {
      this.jobsNames = response;
    });
  }
}
