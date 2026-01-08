import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingsDiscordComponent } from "./settings-discord.component";
import { provideToastr } from "ngx-toastr";
import { DiscordSettingsServiceMock } from "../../../../../testing/mocks/discord-settings-service.mock";
import { DiscordSettingsService } from "../../../service/discord-settings.service";
import { EMPTY } from "rxjs";

describe("SettingsDiscordComponent", () => {
  let component: SettingsDiscordComponent;
  let fixture: ComponentFixture<SettingsDiscordComponent>;

  const discordSettingsServiceMock = new DiscordSettingsServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsDiscordComponent],
      providers: [provideToastr(), {
        provide: DiscordSettingsService, useValue: discordSettingsServiceMock
      }]
    }).compileComponents();

    discordSettingsServiceMock.getDiscordSettings.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(SettingsDiscordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
