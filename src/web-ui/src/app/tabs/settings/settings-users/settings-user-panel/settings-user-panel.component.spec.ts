import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingsUserPanelComponent } from "./settings-user-panel.component";
import { inputBinding } from "@angular/core";

describe("SettingsUserPanelComponent", () => {
  let component: SettingsUserPanelComponent;
  let fixture: ComponentFixture<SettingsUserPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsUserPanelComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(SettingsUserPanelComponent, {
      bindings: [
        inputBinding("user", () => {
          return {
            id: 1,
            username: "user",
            password: "test",
            authorities: [],
            locked: false,
            lastLoginDate: new Date().toDateString()
          };
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
