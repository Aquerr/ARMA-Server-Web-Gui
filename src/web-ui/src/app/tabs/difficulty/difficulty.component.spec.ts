import { ComponentFixture, TestBed } from "@angular/core/testing";

import { DifficultyComponent } from "./difficulty.component";
import { provideToastr } from "ngx-toastr";
import { ServerDifficultyServiceMock } from "../../../../testing/mocks/server-difficulty-service.mock";
import { ServerDifficultyService } from "../../service/server-difficulty.service";
import { EMPTY } from "rxjs";

describe("DifficultyComponent", () => {
  let component: DifficultyComponent;
  let fixture: ComponentFixture<DifficultyComponent>;

  const serverDifficultyServiceMock = new ServerDifficultyServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DifficultyComponent],
      providers: [provideToastr(), {
        provide: ServerDifficultyService, useValue: serverDifficultyServiceMock
      }]
    }).compileComponents();

    serverDifficultyServiceMock.getDifficulties.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(DifficultyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
