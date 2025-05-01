import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingsUsersComponent } from "./settings-users.component";

describe("SettingsUsersComponent", () => {
  let component: SettingsUsersComponent;
  let fixture: ComponentFixture<SettingsUsersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsUsersComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(SettingsUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
