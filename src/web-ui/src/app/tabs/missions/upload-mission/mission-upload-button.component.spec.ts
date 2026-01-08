import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MissionUploadButtonComponent } from "./mission-upload-button.component";
import { provideToastr } from "ngx-toastr";

describe("MissionUploadButtonComponent", () => {
  let component: MissionUploadButtonComponent;
  let fixture: ComponentFixture<MissionUploadButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MissionUploadButtonComponent],
      providers: [provideToastr()]
    }).compileComponents();

    fixture = TestBed.createComponent(MissionUploadButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
