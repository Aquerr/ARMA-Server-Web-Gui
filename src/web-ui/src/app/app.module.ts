import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";

import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { MobileHeaderComponent } from "./mobile-header/mobile-header.component";
import { NetworkComponent } from "./tabs/network/network.component";
import { ModsComponent } from "./tabs/mods/mods.component";
import { MissionsComponent } from "./tabs/missions/missions.component";
import { LoggingComponent } from "./tabs/logging/logging.component";
import { SideMenuComponent } from "./side-menu/side-menu.component";
import { LoginComponent } from "./login/login.component";
import { RouterModule } from "@angular/router";
import { MatListModule } from "@angular/material/list";
import { MatRippleModule } from "@angular/material/core";
import { AswgSpinnerComponent } from "./aswg-spinner/aswg-spinner.component";
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { AswgHttpInterceptor } from "./interceptors/aswg-http.interceptor";
import { MatFormFieldModule } from "@angular/material/form-field";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { ToastrModule } from "ngx-toastr";
import { MatIconModule } from "@angular/material/icon";
import { MissionUploadButtonComponent } from "./tabs/missions/upload-mission/mission-upload-button.component";
import { MatTableModule } from "@angular/material/table";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { SecurityComponent } from "./tabs/security/security.component";
import { MatDialogModule } from "@angular/material/dialog";
import {
  MissionDeleteConfirmDialogComponent
} from "./tabs/missions/mission-delete-confirm-dialog/mission-delete-confirm-dialog.component";
import { DragDropModule } from "@angular/cdk/drag-drop";
import { ModUploadButtonComponent } from "./tabs/mods/mod-upload-button/mod-upload-button.component";
import {
  ModDeleteConfirmDialogComponent
} from "./tabs/mods/mod-delete-confirm-dialog/mod-delete-confirm-dialog.component";
import { MatSelectModule } from "@angular/material/select";
import { MissionModifyDialogComponent } from "./tabs/missions/mission-modify-dialog/mission-modify-dialog.component";
import {
  MissionParameterComponent
} from "./tabs/missions/mission-modify-dialog/mission-parameter/mission-parameter.component";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { WorkshopComponent } from "./tabs/workshop/workshop.component";
import { NgOptimizedImage } from "@angular/common";
import { WorkshopItemComponent } from "./tabs/workshop/workshop-item/workshop-item.component";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatMenuModule } from "@angular/material/menu";
import { ModPresetsComponent } from "./tabs/mods/mod-presets/mod-presets.component";
import { ModPresetItemComponent } from "./tabs/mods/mod-presets/mod-preset-item/mod-preset-item.component";
import {
  ModPresetAddDialogComponent
} from "./tabs/mods/mod-presets/mod-preset-add-dialog/mod-preset-add-dialog.component";
import {
  ModPresetSaveDialogComponent
} from "./tabs/mods/mod-presets/mod-preset-save-dialog/mod-preset-save-dialog.component";
import {
  ModPresetDeleteDialogComponent
} from "./tabs/mods/mod-presets/mod-preset-delete-dialog/mod-preset-delete-dialog.component";
import {
  ModForceUpdateConfirmDialogComponent
} from "./tabs/mods/mod-force-update-confirm-dialog/mod-force-update-confirm-dialog.component";
import { SettingsComponent } from "./tabs/settings/settings.component";
import { MatChipsModule } from "@angular/material/chips";
import {
  VoteCmdListItemComponent
} from "./tabs/security/vote-cmds-list/vote-cmd-list-item/vote-cmd-list-item.component";
import { VoteCmdsListComponent } from "./tabs/security/vote-cmds-list/vote-cmds-list.component";
import { MatCardModule } from "@angular/material/card";
import { DifficultyComponent } from "./tabs/difficulty/difficulty.component";
import { DifficultyPanelComponent } from "./tabs/difficulty/difficulty-panel/difficulty-panel.component";
import {
  DifficultyDeleteConfirmDialogComponent
} from "./tabs/difficulty/difficulty-delete-confirm-dialog/difficulty-delete-confirm-dialog.component";
import { NewMissionDialogComponent } from "./tabs/missions/new-mission-dialog/new-mission-dialog.component";
import { FileUploadSnackBarComponent } from "./common-ui/file-upload-snack-bar/file-upload-snack-bar.component";
import { CommonConfirmDialogComponent } from "./common-ui/common-confirm-dialog/common-confirm-dialog.component";
import { MatPaginator } from "@angular/material/paginator";
import { MatAutocomplete, MatAutocompleteTrigger } from "@angular/material/autocomplete";
import { DesktopHeaderComponent } from "./desktop-header/desktop-header.component";
import { AswgChipFormInputComponent } from "./common-ui/aswg-chip-form-input/aswg-chip-form-input.component";
import { DragDropOverlay } from "./common-ui/drag-and-drop-overlay/drag-and-drop-overlay.component";
import { DragAndDropFileDirective } from "./common-ui/directive/drag-and-drop-file.directive";
import { NotManagedModsComponent } from "./tabs/mods/not-managed-mods/not-managed-mods.component";
import { ModListsComponent } from "./tabs/mods/mod-lists/mod-lists.component";
import { FilesizePipe } from "./util/pipe/filesize.pipe";
import { LoadingSpinnerMaskService } from "./service/loading-spinner-mask.service";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";

@NgModule({
  declarations: [
    AppComponent,
    DesktopHeaderComponent,
    MobileHeaderComponent,
    NetworkComponent,
    ModsComponent,
    MissionsComponent,
    LoggingComponent,
    SideMenuComponent,
    LoginComponent,
    AswgSpinnerComponent,
    MissionUploadButtonComponent,
    SecurityComponent,
    VoteCmdListItemComponent,
    VoteCmdsListComponent,
    MissionDeleteConfirmDialogComponent,
    ModUploadButtonComponent,
    ModDeleteConfirmDialogComponent,
    MissionModifyDialogComponent,
    MissionParameterComponent,
    WorkshopComponent,
    WorkshopItemComponent,
    ModPresetsComponent,
    ModPresetItemComponent,
    ModPresetAddDialogComponent,
    ModPresetSaveDialogComponent,
    ModPresetDeleteDialogComponent,
    ModForceUpdateConfirmDialogComponent,
    SettingsComponent,
    DifficultyComponent,
    DifficultyPanelComponent,
    DifficultyDeleteConfirmDialogComponent,
    NewMissionDialogComponent,
    CommonConfirmDialogComponent
  ],
  bootstrap: [AppComponent],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    AppRoutingModule,
    RouterModule,
    MatListModule,
    MatRippleModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatCheckboxModule,
    MatDialogModule,
    ReactiveFormsModule,
    FormsModule,
    DragDropModule,
    ToastrModule.forRoot(),
    MatSelectModule,
    MatProgressBarModule,
    MatSnackBarModule,
    NgOptimizedImage,
    MatExpansionModule,
    MatTooltipModule,
    MatMenuModule,
    MatChipsModule,
    MatCardModule,
    MatPaginator,
    MatAutocomplete,
    MatAutocompleteTrigger,
    AswgChipFormInputComponent,
    DragDropOverlay,
    DragAndDropFileDirective,
    NotManagedModsComponent,
    ModListsComponent,
    FilesizePipe,
    FileUploadSnackBarComponent
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AswgHttpInterceptor, multi: true },
    provideHttpClient(withInterceptorsFromDi()),
    LoadingSpinnerMaskService
  ]
})
export class AppModule {
}
