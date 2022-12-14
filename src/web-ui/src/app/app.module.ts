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
import { UploadMissionComponent } from './tabs/missions/upload-mission/upload-mission.component';
import { DragAndDropFileDirective } from './common-ui/directive/drag-and-drop-file.directive';
import {MatTableModule} from "@angular/material/table";
import {MatCheckboxModule} from "@angular/material/checkbox";
import { SecurityComponent } from './tabs/security/security.component';
import { MatDialogModule} from "@angular/material/dialog";
import { MissionDeleteConfirmDialogComponent } from './tabs/missions/mission-delete-confirm-dialog/mission-delete-confirm-dialog.component';
import { MotdListComponent } from './tabs/general/motd-list/motd-list.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { StatusComponent } from './tabs/status/status.component';
import {AswgDragDropListComponent} from "./common-ui/aswg-drag-drop-list/aswg-drag-drop-list.component";
import { UploadModComponent } from './tabs/mods/upload-mod/upload-mod.component';
import {
  ModDeleteConfirmDialogComponent
} from "./tabs/mods/mod-delete-confirm-dialog/mod-delete-confirm-dialog.component";
import {MatSelectModule} from "@angular/material/select";
import { MissionModifyDialogComponent } from './tabs/missions/mission-modify-dialog/mission-modify-dialog.component';
import { MissionParameterComponent } from './tabs/missions/mission-modify-dialog/mission-parameter/mission-parameter.component';
import {ModsListComponent} from "./tabs/mods/mods-list/mods-list.component";
import {MissionsListComponent} from "./tabs/missions/missions-list/missions-list.component";

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
    UploadMissionComponent,
    DragAndDropFileDirective,
    SecurityComponent,
    MissionDeleteConfirmDialogComponent,
    MotdListComponent,
    StatusComponent,
    AswgDragDropListComponent,
    ModsListComponent,
    UploadModComponent,
    ModDeleteConfirmDialogComponent,
    MissionModifyDialogComponent,
    MissionParameterComponent,
    MissionsListComponent
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
        MatSelectModule
    ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AswgHttpInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
