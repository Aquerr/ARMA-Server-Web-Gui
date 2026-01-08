import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingsSteamComponent } from "./settings-steam.component";
import { provideToastr } from "ngx-toastr";
import { SteamSettingsService } from "../../../service/steam-settings.service";
import { SteamServiceMock } from "../../../../../testing/mocks/steam-service.mock";
import { EMPTY } from "rxjs";

describe("SettingsSteamComponent", () => {
  let component: SettingsSteamComponent;
  let fixture: ComponentFixture<SettingsSteamComponent>;

  const steamSettingsServiceMock = new SteamServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsSteamComponent],
      providers: [provideToastr(),
        {
          provide: SteamSettingsService, useValue: steamSettingsServiceMock
        }]
    })
      .compileComponents();

    steamSettingsServiceMock.getSteamSettings.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(SettingsSteamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
