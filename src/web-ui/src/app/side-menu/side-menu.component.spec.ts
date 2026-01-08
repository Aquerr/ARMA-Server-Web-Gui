import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SideMenuComponent } from "./side-menu.component";
import { provideToastr } from "ngx-toastr";

describe("SideMenuComponent", () => {
  let component: SideMenuComponent;
  let fixture: ComponentFixture<SideMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SideMenuComponent],
      providers: [provideToastr()]
    }).compileComponents();

    fixture = TestBed.createComponent(SideMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
