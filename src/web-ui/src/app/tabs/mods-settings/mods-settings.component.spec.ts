import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModsSettingsComponent } from "./mods-settings.component";
import { ModSettingsServiceMock } from "../../../../testing/mocks/mod-settings-service.mock";
import { ModSettingsService } from "@service/mod-settings.service";
import { EMPTY } from "rxjs";

describe("ModsSettingsComponent", () => {
  let component: ModsSettingsComponent;
  let fixture: ComponentFixture<ModsSettingsComponent>;

  const modSettingsServiceMock = new ModSettingsServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModsSettingsComponent],
      providers: [{
        provide: ModSettingsService, useValue: modSettingsServiceMock
      }]
    }).compileComponents();

    modSettingsServiceMock.getAllModSettings.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(ModsSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
