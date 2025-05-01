import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModSettingsPanelComponent } from "./mod-settings-panel.component";

describe("ModSettingsPanelComponent", () => {
  let component: ModSettingsPanelComponent;
  let fixture: ComponentFixture<ModSettingsPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModSettingsPanelComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ModSettingsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
