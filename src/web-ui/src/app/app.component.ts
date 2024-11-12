import {Component, HostListener, OnInit} from '@angular/core';
import {ThemeService} from './service/util/theme.service';
import {ApplicationService} from "./service/application.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'arma-web-gui';
  isMobile: boolean = false;
  version: string = "";

  constructor(private themeService: ThemeService,
              private applicationService: ApplicationService) {
  }

  ngOnInit() {
    this.themeService.setThemeOnAppInit();
    this.isMobileView();
    this.applicationService.getApplicationInfo().subscribe(response => {
      this.version = response.application.version;
    });
  }

  changeTheme() {
    this.themeService.changeTheme();
  }

  isDarkMode() {
    return this.themeService.isDarkMode();
  }

  @HostListener('window:resize', ['$event'])
    isMobileView() {
      this.isMobile = window.innerWidth < 800;
    }
}
