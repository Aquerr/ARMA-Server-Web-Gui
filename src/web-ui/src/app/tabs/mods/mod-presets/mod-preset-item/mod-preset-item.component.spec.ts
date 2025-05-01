import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModPresetItemComponent } from "./mod-preset-item.component";

describe("ModPresetItemComponent", () => {
  let component: ModPresetItemComponent;
  let fixture: ComponentFixture<ModPresetItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ModPresetItemComponent]
    });
    fixture = TestBed.createComponent(ModPresetItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
