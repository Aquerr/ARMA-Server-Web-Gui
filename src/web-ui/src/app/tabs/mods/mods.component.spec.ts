import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModsComponent } from "./mods.component";

describe("ModsComponent", () => {
  let component: ModsComponent;
  let fixture: ComponentFixture<ModsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModsComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ModsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
