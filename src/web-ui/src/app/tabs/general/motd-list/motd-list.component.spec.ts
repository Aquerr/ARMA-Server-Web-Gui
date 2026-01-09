import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MotdListComponent } from "./motd-list.component";
import { FormControl } from "@angular/forms";
import { inputBinding } from "@angular/core";

describe("MotdListComponent", () => {
  let component: MotdListComponent;
  let fixture: ComponentFixture<MotdListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MotdListComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(MotdListComponent, {
      bindings: [
        inputBinding("motdControl", () => new FormControl()),
        inputBinding("motdIntervalControl", () => new FormControl())
      ]
    });
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
