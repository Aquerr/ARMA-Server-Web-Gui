import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {GeneralComponent} from "./tabs/general/general.component";
import {NetworkComponent} from "./tabs/network/network.component";
import {MissionsComponent} from "./tabs/missions/missions.component";
import {ModsComponent} from "./tabs/mods/mods.component";
import {LoggingComponent} from "./tabs/logging/logging.component";
import {LoginComponent} from "./login/login.component";
import {AuthService} from "./service/auth.service";
import {SecurityComponent} from "./tabs/security/security.component";
import {StatusComponent} from "./tabs/status/status.component";

const routes: Routes = [
  {path: '', redirectTo: '/status', pathMatch: "full"},
  {path: 'status', component: StatusComponent, canActivate: [AuthService]},
  {path: 'general', component: GeneralComponent, canActivate: [AuthService]},
  {path: 'security', component: SecurityComponent, canActivate: [AuthService]},
  {path: 'network', component: NetworkComponent, canActivate: [AuthService]},
  {path: 'missions', component: MissionsComponent, canActivate: [AuthService]},
  {path: 'mods', component: ModsComponent, canActivate: [AuthService]},
  {path: 'logging', component: LoggingComponent, canActivate: [AuthService]},
  {path: 'login', component: LoginComponent},
  {path: '**', redirectTo: 'status'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
