import {Component, HostListener, OnInit} from '@angular/core';
import {ThemeService} from './service/util/theme.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'arma-web-gui';
  isMobile: boolean = false;

  constructor(private themeService: ThemeService) {
  }

  ngOnInit() {
    this.themeService.setThemeOnAppInit();
    this.isMobileView();
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
