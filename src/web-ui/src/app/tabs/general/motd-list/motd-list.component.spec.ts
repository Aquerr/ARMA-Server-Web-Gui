import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MotdListComponent } from "./motd-list.component";

describe("MotdListComponent", () => {
  let component: MotdListComponent;
  let fixture: ComponentFixture<MotdListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MotdListComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(MotdListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
