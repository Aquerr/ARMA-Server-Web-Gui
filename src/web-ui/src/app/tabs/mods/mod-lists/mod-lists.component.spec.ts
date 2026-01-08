import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModListsComponent } from "./mod-lists.component";
import { provideToastr } from "ngx-toastr";
import { ServerModsService } from "../../../service/server-mods.service";
import { ServerModsServiceMock } from "../../../../../testing/mocks/server-mods-service.mock";
import { EMPTY } from "rxjs";

describe("ModListsComponent", () => {
  let component: ModListsComponent;
  let fixture: ComponentFixture<ModListsComponent>;

  const serverModsServiceMock = new ServerModsServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModListsComponent],
      providers: [provideToastr(), {
        provide: ServerModsService, useValue: serverModsServiceMock
      }]
    })
      .compileComponents();

    serverModsServiceMock.getInstalledMods.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(ModListsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
