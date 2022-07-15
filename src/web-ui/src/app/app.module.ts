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
import {AuthInterceptor} from "./interceptors/auth.interceptor";
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {ToastrModule} from "ngx-toastr";

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
    AswgSpinnerComponent
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
    NgxSpinnerModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    ToastrModule.forRoot()
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
