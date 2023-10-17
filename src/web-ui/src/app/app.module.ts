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
import {MatListModule} from "@angular/material/list";
import {MatRippleModule} from "@angular/material/core";
import { AswgSpinnerComponent } from './aswg-spinner/aswg-spinner.component';
import {NgxSpinnerModule} from "ngx-spinner";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {AswgHttpInterceptor} from "./interceptors/aswg-http.interceptor";
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {ToastrModule} from "ngx-toastr";
import {MatIconModule} from "@angular/material/icon";
import { MissionUploadButtonComponent } from './tabs/missions/upload-mission/mission-upload-button.component';
import { DragAndDropFileDirective } from './common-ui/directive/drag-and-drop-file.directive';
import {MatTableModule} from "@angular/material/table";
import {MatCheckboxModule} from "@angular/material/checkbox";
import { SecurityComponent } from './tabs/security/security.component';
import {MatDialogModule} from "@angular/material/dialog";
import { MissionDeleteConfirmDialogComponent } from './tabs/missions/mission-delete-confirm-dialog/mission-delete-confirm-dialog.component';
import { MotdListComponent } from './tabs/general/motd-list/motd-list.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { StatusComponent } from './tabs/status/status.component';
import {AswgDragDropListComponent} from "./common-ui/aswg-drag-drop-list/aswg-drag-drop-list.component";
import { ModUploadButtonComponent } from './tabs/mods/mod-upload-button/mod-upload-button.component';
import {
  ModDeleteConfirmDialogComponent
} from "./tabs/mods/mod-delete-confirm-dialog/mod-delete-confirm-dialog.component";
import {MatSelectModule} from "@angular/material/select";
import { MissionModifyDialogComponent } from './tabs/missions/mission-modify-dialog/mission-modify-dialog.component';
import { MissionParameterComponent } from './tabs/missions/mission-modify-dialog/mission-parameter/mission-parameter.component';
import {MatProgressBarModule} from "@angular/material/progress-bar";
import { ModUploadSnackBarComponent } from './tabs/mods/mod-upload-snack-bar/mod-upload-snack-bar.component';
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {
  MissionUploadSnackBarComponent
} from "./tabs/missions/mission-upload-snack-bar/mission-upload-snack-bar.component";
import {WorkshopComponent} from './tabs/workshop/workshop.component';
import {NgOptimizedImage} from '@angular/common';
import {WorkshopItemComponent} from './tabs/workshop/workshop-item/workshop-item.component';
import {ModListItemComponent} from "./tabs/mods/mod-list-item/mod-list-item.component";
import {MatExpansionModule} from "@angular/material/expansion";
import {PlayerListComponent} from "./tabs/status/player-list/player-list.component";
import { ServerConsoleComponent } from './tabs/status/server-console/server-console.component';

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
    WorkshopItemComponent,
    ModListItemComponent,
    PlayerListComponent,
    PlayerListComponent,
    ServerConsoleComponent
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
    NgOptimizedImage,
    MatExpansionModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AswgHttpInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
