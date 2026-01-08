import { ComponentFixture, TestBed } from "@angular/core/testing";

import { DesktopHeaderComponent } from "./desktop-header.component";
import { provideRouter } from "@angular/router";

describe("HeaderComponent", () => {
  let component: DesktopHeaderComponent;
  let fixture: ComponentFixture<DesktopHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DesktopHeaderComponent],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(DesktopHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
