import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingsUsersComponent } from "./settings-users.component";
import { provideToastr } from "ngx-toastr";
import { UsersServiceMock } from "../../../../../testing/mocks/users-service.mock";
import { UsersService } from "../../../service/users.service";
import { EMPTY } from "rxjs";

describe("SettingsUsersComponent", () => {
  let component: SettingsUsersComponent;
  let fixture: ComponentFixture<SettingsUsersComponent>;

  const usersServiceMock = new UsersServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsUsersComponent],
      providers: [provideToastr(), {
        provide: UsersService, useValue: usersServiceMock
      }]
    }).compileComponents();

    usersServiceMock.getUsers.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(SettingsUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
