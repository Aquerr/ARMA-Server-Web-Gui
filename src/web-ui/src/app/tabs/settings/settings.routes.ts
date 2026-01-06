import { Routes } from "@angular/router";
import { SettingsComponent } from "./settings.component";
import { hasAllAuthorities, isAuthenticated } from "../../service/permission.service";
import { AswgAuthority } from "../../model/authority.model";

export const settingsRoutes: Routes = [
  {
    path: "settings",
    children: [
      {
        path: "",
        component: SettingsComponent,
        canActivate: [isAuthenticated()]
      },
      {
        path: "users",
        loadComponent: () =>
          import("./settings-users/settings-users.component").then(
            (c) => c.SettingsUsersComponent
          ),
        canActivate: [hasAllAuthorities([AswgAuthority.USERS_VIEW])]
      },
      {
        path: "discord",
        loadComponent: () =>
          import("./settings-discord/settings-discord.component").then(
            (c) => c.SettingsDiscordComponent
          ),
        canActivate: [hasAllAuthorities([AswgAuthority.DISCORD_SETTINGS_UPDATE])]
      },
      {
        path: "steam",
        loadComponent: () =>
          import("./settings-steam/settings-steam.component").then(
            (c) => c.SettingsSteamComponent
          ),
        canActivate: [hasAllAuthorities([AswgAuthority.STEAM_SETTINGS_UPDATE])]
      },
      {
        path: "jobs",
        children: [
          {
            path: "",
            loadComponent: () =>
              import("./settings-jobs/settings-jobs.component").then(
                (c) => c.SettingsJobsComponent
              ),
            canActivate: [hasAllAuthorities([AswgAuthority.JOBS_SETTINGS_UPDATE])]
          },
          {
            path: ":name",
            loadComponent: () => import("./settings-jobs/job-view/job-view.component").then((c) => c.JobViewComponent),
            canActivate: [hasAllAuthorities([AswgAuthority.JOBS_SETTINGS_UPDATE])]
          }
        ]
      }
    ]
  }
];
