import { ComponentFixture, TestBed } from "@angular/core/testing";

import { DifficultyPanelComponent } from "./difficulty-panel.component";
import { provideToastr } from "ngx-toastr";
import { DifficultyProfile } from "../../../model/difficulty-profile.model";

describe("DifficultyPanelComponent", () => {
  let component: DifficultyPanelComponent;
  let fixture: ComponentFixture<DifficultyPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DifficultyPanelComponent],
      providers: [provideToastr()]
    }).compileComponents();

    fixture = TestBed.createComponent(DifficultyPanelComponent);
    component = fixture.componentInstance;
    component.difficultyProfile = {
      id: 1,
      name: "test",
      active: true,
      options: {}
    } as DifficultyProfile;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
