import { ComponentFixture, TestBed } from "@angular/core/testing";

import { DifficultyPanelComponent } from "./difficulty-panel.component";
import { inputBinding } from "@angular/core";

describe("DifficultyPanelComponent", () => {
  let component: DifficultyPanelComponent;
  let fixture: ComponentFixture<DifficultyPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DifficultyPanelComponent],
      providers: []
    }).compileComponents();

    fixture = TestBed.createComponent(DifficultyPanelComponent, {
      bindings: [
        inputBinding("difficultyProfile", () => {
          return {
            id: 1,
            name: "test",
            active: true,
            options: {}
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
