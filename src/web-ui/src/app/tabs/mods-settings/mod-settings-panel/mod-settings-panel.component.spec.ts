import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModSettingsPanelComponent } from "./mod-settings-panel.component";
import { inputBinding } from "@angular/core";

describe("ModSettingsPanelComponent", () => {
  let component: ModSettingsPanelComponent;
  let fixture: ComponentFixture<ModSettingsPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModSettingsPanelComponent],
      providers: []
    }).compileComponents();

    fixture = TestBed.createComponent(ModSettingsPanelComponent, {
      bindings: [
        inputBinding("modSettings", () => {
          return {};
        })
      ]
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
