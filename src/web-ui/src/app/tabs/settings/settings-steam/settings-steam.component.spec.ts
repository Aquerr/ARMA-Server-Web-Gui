import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingsSteamComponent } from "./settings-steam.component";

describe("SettingsSteamComponent", () => {
  let component: SettingsSteamComponent;
  let fixture: ComponentFixture<SettingsSteamComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsSteamComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SettingsSteamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
