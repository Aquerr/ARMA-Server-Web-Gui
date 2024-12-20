import {inject, NgModule} from '@angular/core';
import {provideRouter, RouterModule, Routes} from '@angular/router';
import {GeneralComponent} from "./tabs/general/general.component";
import {NetworkComponent} from "./tabs/network/network.component";
import {MissionsComponent} from "./tabs/missions/missions.component";
import {ModsComponent} from "./tabs/mods/mods.component";
import {LoggingComponent} from "./tabs/logging/logging.component";
import {LoginComponent} from "./login/login.component";
import {AuthService} from "./service/auth.service";
import {SecurityComponent} from "./tabs/security/security.component";
import {StatusComponent} from "./tabs/status/status.component";
import {WorkshopComponent} from './tabs/workshop/workshop.component';
import {WorkshopService} from "./service/workshop.service";
import {map} from "rxjs";
import {SettingsComponent} from "./tabs/settings/settings.component";
import {DifficultyComponent} from "./tabs/difficulty/difficulty.component";
import {ModsSettingsComponent} from "./tabs/mods-settings/mods-settings.component";

const routes: Routes = [
  {path: 'status', component: StatusComponent, canActivate: [AuthService]},
  {path: 'general', component: GeneralComponent, canActivate: [AuthService]},
  {path: 'security', component: SecurityComponent, canActivate: [AuthService]},
  {path: 'network', component: NetworkComponent, canActivate: [AuthService]},
  {path: 'difficulty', component: DifficultyComponent, canActivate: [AuthService]},
  {path: 'missions', component: MissionsComponent, canActivate: [AuthService]},
  {path: 'mods', component: ModsComponent, canActivate: [AuthService]},
  {path: 'mods-settings', component: ModsSettingsComponent, canActivate: [AuthService]},
  {path: 'logging', component: LoggingComponent, canActivate: [AuthService]},
  {path: 'workshop', component: WorkshopComponent,
    canMatch: [() => inject(AuthService).isAuthenticated() && inject(WorkshopService).canUseWorkshop().pipe(map(response => response.active))]
  },
  {path: 'settings', component: SettingsComponent, canActivate: [AuthService]},
  {path: 'login', component: LoginComponent},
  {path: '', redirectTo: '/status', pathMatch: "full"},
  {path: '**', redirectTo: 'status'}
];

@NgModule({
  exports: [RouterModule],
  providers: [provideRouter(routes)]
})
export class AppRoutingModule { }
