import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModSettingsPanelComponent } from "./mod-settings-panel.component";
import { provideToastr } from "ngx-toastr";
import { ModSettings } from "../../../model/mod-settings.model";

describe("ModSettingsPanelComponent", () => {
  let component: ModSettingsPanelComponent;
  let fixture: ComponentFixture<ModSettingsPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModSettingsPanelComponent],
      providers: [provideToastr()]
    }).compileComponents();

    fixture = TestBed.createComponent(ModSettingsPanelComponent);
    component = fixture.componentInstance;
    component.modSettings = {} as ModSettings;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
