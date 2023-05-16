import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { GeneralComponent } from './tabs/general/general.component';
import { NetworkComponent } from './tabs/network/network.component';
import { ModsComponent } from './tabs/mods/mods.component';
import { MissionsComponent } from './tabs/missions/missions.component';
import { LoggingComponent } from './tabs/logging/logging.component';
import { SideMenuComponent } from './side-menu/side-menu.component';
import { LoginComponent } from './login/login.component';
import {RouterModule} from "@angular/router";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatLegacyListModule as MatListModule} from "@angular/material/legacy-list";
import {MatRippleModule} from "@angular/material/core";
import { AswgSpinnerComponent } from './aswg-spinner/aswg-spinner.component';
import {NgxSpinnerModule} from "ngx-spinner";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {AswgHttpInterceptor} from "./interceptors/aswg-http.interceptor";
import {MatLegacyFormFieldModule as MatFormFieldModule} from "@angular/material/legacy-form-field";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatLegacyInputModule as MatInputModule} from "@angular/material/legacy-input";
import {MatLegacyButtonModule as MatButtonModule} from "@angular/material/legacy-button";
import {ToastrModule} from "ngx-toastr";
import {MatIconModule} from "@angular/material/icon";
import { MissionUploadButtonComponent } from './tabs/missions/upload-mission/mission-upload-button.component';
import { DragAndDropFileDirective } from './common-ui/directive/drag-and-drop-file.directive';
import {MatLegacyTableModule as MatTableModule} from "@angular/material/legacy-table";
import {MatLegacyCheckboxModule as MatCheckboxModule} from "@angular/material/legacy-checkbox";
import { SecurityComponent } from './tabs/security/security.component';
import { MatLegacyDialogModule as MatDialogModule} from "@angular/material/legacy-dialog";
import { MissionDeleteConfirmDialogComponent } from './tabs/missions/mission-delete-confirm-dialog/mission-delete-confirm-dialog.component';
import { MotdListComponent } from './tabs/general/motd-list/motd-list.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { StatusComponent } from './tabs/status/status.component';
import {AswgDragDropListComponent} from "./common-ui/aswg-drag-drop-list/aswg-drag-drop-list.component";
import { ModUploadButtonComponent } from './tabs/mods/mod-upload-button/mod-upload-button.component';
import {
  ModDeleteConfirmDialogComponent
} from "./tabs/mods/mod-delete-confirm-dialog/mod-delete-confirm-dialog.component";
import {MatLegacySelectModule as MatSelectModule} from "@angular/material/legacy-select";
import { MissionModifyDialogComponent } from './tabs/missions/mission-modify-dialog/mission-modify-dialog.component';
import { MissionParameterComponent } from './tabs/missions/mission-modify-dialog/mission-parameter/mission-parameter.component';
import {MatLegacyProgressBarModule as MatProgressBarModule} from "@angular/material/legacy-progress-bar";
import { ModUploadSnackBarComponent } from './tabs/mods/mod-upload-snack-bar/mod-upload-snack-bar.component';
import {MatLegacySnackBarModule as MatSnackBarModule} from "@angular/material/legacy-snack-bar";
import {
  MissionUploadSnackBarComponent
} from "./tabs/missions/mission-upload-snack-bar/mission-upload-snack-bar.component";
import {WorkshopComponent} from './tabs/workshop/workshop.component';
import {NgOptimizedImage} from '@angular/common';
import {WorkshopItemComponent} from './tabs/workshop/workshop-item/workshop-item.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    GeneralComponent,
    NetworkComponent,
    ModsComponent,
    MissionsComponent,
    LoggingComponent,
    SideMenuComponent,
    LoginComponent,
    AswgSpinnerComponent,
    MissionUploadButtonComponent,
    DragAndDropFileDirective,
    SecurityComponent,
    MissionDeleteConfirmDialogComponent,
    MotdListComponent,
    StatusComponent,
    AswgDragDropListComponent,
    ModUploadButtonComponent,
    ModDeleteConfirmDialogComponent,
    MissionModifyDialogComponent,
    MissionParameterComponent,
    ModUploadSnackBarComponent,
    MissionUploadSnackBarComponent,
    WorkshopComponent,
    WorkshopItemComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule,
    BrowserAnimationsModule,
    MatListModule,
    MatRippleModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatCheckboxModule,
    MatDialogModule,
    NgxSpinnerModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    DragDropModule,
    ToastrModule.forRoot(),
    MatSelectModule,
    MatProgressBarModule,
    MatSnackBarModule,
    NgOptimizedImage
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AswgHttpInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
