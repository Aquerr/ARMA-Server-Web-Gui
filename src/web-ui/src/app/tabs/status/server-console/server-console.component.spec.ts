import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ServerConsoleComponent } from "./server-console.component";

describe("ServerConsoleComponent", () => {
  let component: ServerConsoleComponent;
  let fixture: ComponentFixture<ServerConsoleComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServerConsoleComponent]
    });
    fixture = TestBed.createComponent(ServerConsoleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
