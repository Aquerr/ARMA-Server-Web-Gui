import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingsUserPanelComponent } from "./settings-user-panel.component";
import { AswgUser } from "../../../../service/users.service";

describe("SettingsUserPanelComponent", () => {
  let component: SettingsUserPanelComponent;
  let fixture: ComponentFixture<SettingsUserPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsUserPanelComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(SettingsUserPanelComponent);
    component = fixture.componentInstance;
    component.user = {
      id: 1,
      username: "user",
      password: "test",
      authorities: [],
      locked: false,
      lastLoginDate: new Date().toDateString()
    } as AswgUser;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
