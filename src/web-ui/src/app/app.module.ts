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
import { ListMissionsComponent } from './tabs/missions/list-missions/list-missions.component';
import { DragAndDropFileDirective } from './directive/drag-and-drop-file.directive';
import {MatTableModule} from "@angular/material/table";
import {MatCheckboxModule} from "@angular/material/checkbox";
import { SecurityComponent } from './tabs/security/security.component';
import { MatDialogModule} from "@angular/material/dialog";
import { MissionDeleteConfirmDialogComponent } from './tabs/missions/list-missions/mission-delete-confirm-dialog/mission-delete-confirm-dialog.component';
import { MotdListComponent } from './tabs/general/motd-list/motd-list.component';
import { DragDropModule } from '@angular/cdk/drag-drop';

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
    ListMissionsComponent,
    DragAndDropFileDirective,
    SecurityComponent,
    MissionDeleteConfirmDialogComponent,
    MotdListComponent
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
    ToastrModule.forRoot()
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AswgHttpInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
