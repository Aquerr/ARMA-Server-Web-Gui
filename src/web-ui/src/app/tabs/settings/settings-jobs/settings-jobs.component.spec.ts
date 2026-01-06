import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingsJobsComponent } from "./settings-jobs.component";

describe("SettingsJobsComponent", () => {
  let component: SettingsJobsComponent;
  let fixture: ComponentFixture<SettingsJobsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsJobsComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SettingsJobsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
