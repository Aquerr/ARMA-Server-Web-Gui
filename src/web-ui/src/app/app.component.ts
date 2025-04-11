import {Component, HostListener, OnInit} from '@angular/core';
import {ThemeService} from './service/util/theme.service';
import {ApplicationService} from "./service/application.service";
import {DragFileService} from "./service/drag-file.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    standalone: false
})
export class AppComponent implements OnInit {
  title = 'arma-web-gui';
  isMobile: boolean = false;
  version: string = "";
  isDragEnabled: boolean = false;

  constructor(private themeService: ThemeService,
              private applicationService: ApplicationService,
              private dragFileService: DragFileService) {
  }

  ngOnInit() {
    this.themeService.setThemeOnAppInit();
    this.isMobileView();
    this.applicationService.getApplicationInfo().subscribe(response => {
      this.version = response.application.version;
    });
    this.dragFileService.isDragEnabled$.subscribe({next: (value) => this.isDragEnabled = value });
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
