import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModPresetsComponent } from "./mod-presets.component";
import { provideToastr } from "ngx-toastr";
import { ServerModsServiceMock } from "../../../../../testing/mocks/server-mods-service.mock";
import { ServerModsService } from "../../../service/server-mods.service";
import { EMPTY } from "rxjs";

describe("ModPresetsComponent", () => {
  let component: ModPresetsComponent;
  let fixture: ComponentFixture<ModPresetsComponent>;

  const serverModsServiceMock = new ServerModsServiceMock();

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ModPresetsComponent],
      providers: [provideToastr(), {
        provide: ServerModsService, useValue: serverModsServiceMock
      }]
    });

    serverModsServiceMock.getModPresetsNames.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(ModPresetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
