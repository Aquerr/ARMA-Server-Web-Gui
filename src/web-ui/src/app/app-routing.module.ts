import { inject, NgModule } from "@angular/core";
import { provideRouter, RouterModule, Routes } from "@angular/router";
import { GeneralComponent } from "./tabs/general/general.component";
import { NetworkComponent } from "./tabs/network/network.component";
import { MissionsComponent } from "./tabs/missions/missions.component";
import { ModsComponent } from "./tabs/mods/mods.component";
import { LoggingComponent } from "./tabs/logging/logging.component";
import { LoginComponent } from "./login/login.component";
import { SecurityComponent } from "./tabs/security/security.component";
import { StatusComponent } from "./tabs/status/status.component";
import { WorkshopComponent } from "./tabs/workshop/workshop.component";
import { WorkshopService } from "./service/workshop.service";
import { map } from "rxjs";
import { SettingsComponent } from "./tabs/settings/settings.component";
import { DifficultyComponent } from "./tabs/difficulty/difficulty.component";
import { hasAllAuthorities, isAuthenticated } from "./service/permission.service";
import { AswgAuthority } from "./model/authority.model";

const routes: Routes = [
  { path: "status", component: StatusComponent, canActivate: [isAuthenticated] },
  {
    path: "general",
    component: GeneralComponent,
    canActivate: [hasAllAuthorities([AswgAuthority.GENERAL_SETTINGS_VIEW])]
  },
  {
    path: "security",
    component: SecurityComponent,
    canActivate: [hasAllAuthorities([AswgAuthority.SECURITY_SETTINGS_VIEW])]
  },
  {
    path: "network",
    component: NetworkComponent,
    canActivate: [hasAllAuthorities([AswgAuthority.NETWORK_SETTINGS_VIEW])]
  },
  {
    path: "difficulty",
    component: DifficultyComponent,
    canActivate: [hasAllAuthorities([AswgAuthority.DIFFICULTY_VIEW])]
  },
  {
    path: "missions",
    component: MissionsComponent,
    canActivate: [hasAllAuthorities([AswgAuthority.MISSIONS_VIEW])]
  },
  {
    path: "mods",
    component: ModsComponent,
    canActivate: [hasAllAuthorities([AswgAuthority.MODS_VIEW])]
  },
  {
    path: "cdlc",
    loadComponent: () => import("./tabs/cdlc/cdlc.component").then((c) => c.CdlcComponent),
    canActivate: [hasAllAuthorities([AswgAuthority.CDLC_VIEW])]
  },
  {
    path: "mods-settings",
    loadComponent: () =>
      import("./tabs/mods-settings/mods-settings.component").then((c) => c.ModsSettingsComponent),
    canActivate: [hasAllAuthorities([AswgAuthority.MOD_SETTINGS_VIEW])]
  },
  { path: "logging", component: LoggingComponent, canActivate: [isAuthenticated] },
  {
    path: "workshop",
    component: WorkshopComponent,
    canMatch: [
      () =>
        hasAllAuthorities([AswgAuthority.WORKSHOP_INSTALL]) &&
        inject(WorkshopService)
          .canUseWorkshop()
          .pipe(map((response) => response.active))
    ]
  },
  {
    path: "settings",
    children: [
      {
        path: "",
        component: SettingsComponent,
        canActivate: [isAuthenticated]
      },
      {
        path: "users",
        loadComponent: () =>
          import("./tabs/settings/settings-users/settings-users.component").then(
            (c) => c.SettingsUsersComponent
          ),
        canActivate: [hasAllAuthorities([AswgAuthority.USERS_VIEW])]
      },
      {
        path: "discord",
        loadComponent: () =>
          import("./tabs/settings/settings-discord/settings-discord.component").then(
            (c) => c.SettingsDiscordComponent
          ),
        canActivate: [hasAllAuthorities([AswgAuthority.SECURITY_SETTINGS_VIEW])]
      },
      {
        path: "steam",
        loadComponent: () =>
          import("./tabs/settings/settings-steam/settings-steam.component").then(
            (c) => c.SettingsSteamComponent
          ),
        canActivate: [hasAllAuthorities([AswgAuthority.SECURITY_SETTINGS_VIEW])]
      },
      {
        path: "jobs",
        children: [
          {
            path: '',
            loadComponent: () =>
              import("./tabs/settings/settings-jobs/settings-jobs.component").then(
                (c) => c.SettingsJobsComponent
              ),
            canActivate: [hasAllAuthorities([AswgAuthority.SECURITY_SETTINGS_VIEW])],
          },
          {
            path: ':name',
            loadComponent: () => import("./tabs/settings/settings-jobs/job-view/job-view.component").then(c => c.JobViewComponent),
            canActivate: [hasAllAuthorities([AswgAuthority.SECURITY_SETTINGS_VIEW])],
          }
        ]
      }
    ]
  },
  { path: "login", component: LoginComponent },
  { path: "", redirectTo: "/status", pathMatch: "full" },
  { path: "**", redirectTo: "status" }
];

@NgModule({
  exports: [RouterModule],
  providers: [provideRouter(routes)]
})
export class AppRoutingModule {}
