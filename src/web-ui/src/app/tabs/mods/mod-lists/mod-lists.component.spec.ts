import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModListsComponent } from "./mod-lists.component";

describe("ModListsComponent", () => {
  let component: ModListsComponent;
  let fixture: ComponentFixture<ModListsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModListsComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModListsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
