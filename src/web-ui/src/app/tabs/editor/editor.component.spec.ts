import { ComponentFixture, TestBed } from "@angular/core/testing";

import { EditorComponent } from "./editor.component";
import { provideRouter } from "@angular/router";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { RouterTestingHarness } from "@angular/router/testing";

describe("EditorComponent", () => {
  let component: EditorComponent;
  let fixture: ComponentFixture<EditorComponent>;

  let httpTestingController: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditorComponent],
      providers: [
        provideRouter([{ path: "editor/:file", component: EditorComponent }]),
        provideHttpClientTesting()
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(EditorComponent);
    component = fixture.componentInstance;

    await fixture.whenStable();

    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should fetch config file content on init", async () => {
    const harness = await RouterTestingHarness.create();
    const component = await harness.navigateByUrl("/editor/SERVER_CONFIG", EditorComponent);

    httpTestingController.expectOne("http://localhost:4200/api/v1/editor/server-config").flush({
      content: "config-file-content"
    });

    harness.detectChanges();

    expect(component.codeEditorElement().getCode()).toEqual("config-file-content");
  });
});
