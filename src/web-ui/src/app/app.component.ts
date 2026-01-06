import { Component, OnInit, signal } from "@angular/core";
import { ThemeService } from "./service/util/theme.service";
import { ApplicationService } from "./service/application.service";
import { IconRegistrarService } from "./service/icon-registrar.service";
import { RouterOutlet } from "@angular/router";
import { MatListModule } from "@angular/material/list";
import { MatRippleModule } from "@angular/material/core";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTableModule } from "@angular/material/table";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDialogModule } from "@angular/material/dialog";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { DragDropModule } from "@angular/cdk/drag-drop";
import { ToastrModule } from "ngx-toastr";
import { MatSelectModule } from "@angular/material/select";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatMenuModule } from "@angular/material/menu";
import { MatChipsModule } from "@angular/material/chips";
import { MatCardModule } from "@angular/material/card";
import { AswgSpinnerComponent } from "./aswg-spinner/aswg-spinner.component";
import { SideMenuComponent } from "./side-menu/side-menu.component";
import { MobileHeaderComponent } from "./mobile-header/mobile-header.component";
import { DesktopHeaderComponent } from "./desktop-header/desktop-header.component";
import { NgTemplateOutlet } from "@angular/common";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"],
  host: {
    "(window:resize)": "isMobileView()"
  },
  imports: [
    MatListModule,
    MatRippleModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatCheckboxModule,
    MatDialogModule,
    ReactiveFormsModule,
    FormsModule,
    DragDropModule,
    ToastrModule,
    MatSelectModule,
    MatProgressBarModule,
    MatSnackBarModule,
    MatExpansionModule,
    MatTooltipModule,
    MatMenuModule,
    MatChipsModule,
    MatCardModule,
    AswgSpinnerComponent,
    SideMenuComponent,
    MobileHeaderComponent,
    DesktopHeaderComponent,
    NgTemplateOutlet,
    RouterOutlet
  ]
})
export class AppComponent implements OnInit {
  title = "arma-web-gui";
  isMobile: boolean = false;
  version = signal<string>("");

  constructor(
    private themeService: ThemeService,
    private applicationService: ApplicationService,
    private iconRegistrarService: IconRegistrarService
  ) {
  }

  ngOnInit() {
    this.themeService.setThemeOnAppInit();
    this.isMobileView();
    this.applicationService.getApplicationInfo().subscribe((response) => {
      this.version.set(response.application.version);
    });
  }

  changeTheme() {
    this.themeService.changeTheme();
  }

  isDarkMode() {
    return this.themeService.isDarkMode();
  }

  isMobileView() {
    this.isMobile = window.innerWidth < 800;
  }
}
