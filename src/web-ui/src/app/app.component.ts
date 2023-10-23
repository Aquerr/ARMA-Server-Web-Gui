import {Component, OnInit} from '@angular/core';
import {ThemeService} from './service/util/theme.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'arma-web-gui';

  constructor(private themeService: ThemeService) {
  }

  ngOnInit() {
    this.themeService.setThemeOnAppInit();
  }

  changeTheme() {
    this.themeService.changeTheme();
  }

  isDarkMode() {
    return this.themeService.isDarkMode();
  }
}
