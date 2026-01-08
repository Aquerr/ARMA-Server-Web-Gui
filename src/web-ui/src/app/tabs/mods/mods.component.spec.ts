import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModsComponent } from "./mods.component";
import { provideToastr } from "ngx-toastr";
import { provideRouter } from "@angular/router";
import { ServerModsServiceMock } from "../../../../testing/mocks/server-mods-service.mock";
import { ServerModsService } from "../../service/server-mods.service";
import { EMPTY } from "rxjs";

describe("ModsComponent", () => {
  let component: ModsComponent;
  let fixture: ComponentFixture<ModsComponent>;

  const serverModsServiceMock = new ServerModsServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModsComponent],
      providers: [provideToastr(), provideRouter([]), {
        provide: ServerModsService, useValue: serverModsServiceMock
      }]
    }).compileComponents();

    serverModsServiceMock.getModPresetsNames.mockReturnValue(EMPTY);
    serverModsServiceMock.getInstalledMods.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(ModsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
