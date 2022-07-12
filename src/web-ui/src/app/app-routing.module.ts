import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {GeneralComponent} from "./tabs/general/general.component";
import {NetworkComponent} from "./tabs/network/network.component";
import {MissionsComponent} from "./tabs/missions/missions.component";
import {ModsComponent} from "./tabs/mods/mods.component";
import {LoggingComponent} from "./tabs/logging/logging.component";

const routes: Routes = [
  {path: 'general', component: GeneralComponent},
  {path: 'network', component: NetworkComponent},
  {path: 'missions', component: MissionsComponent},
  {path: 'mods', component: ModsComponent},
  {path: 'logging', component: LoggingComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
