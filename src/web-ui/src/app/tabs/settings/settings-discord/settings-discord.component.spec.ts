import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingsDiscordComponent } from "./settings-discord.component";

describe("SettingsDiscordComponent", () => {
  let component: SettingsDiscordComponent;
  let fixture: ComponentFixture<SettingsDiscordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsDiscordComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(SettingsDiscordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
