import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {

  WHITE_THEME_COLOR = '#4634b7';

  setThemeOnAppInit() {
    const theme = localStorage.getItem('theme');
    if (theme) {
      this.setTheme(theme);
    } else {
      this.setTheme('dark');
    }
  }

  isDarkMode() {
    const theme = localStorage.getItem('theme');
    return theme !== null && theme === 'dark';
  }

  changeTheme() {
    const mainColorValue = document.documentElement.style.getPropertyValue('--aswg-primary-color');
    if (this.WHITE_THEME_COLOR === mainColorValue) {
      this.setTheme('dark');
    } else {
      this.setTheme('light');
    }
  }

  private setTheme(theme: string) {
    this.saveTheme(theme);
    switch (theme) {
      case 'light':
        document.documentElement.style.setProperty('--aswg-primary-color', '#4634b7');
        document.documentElement.style.setProperty('--aswg-primary-color-hover', '#37288f');
        document.documentElement.style.setProperty('--aswg-primary-text-color', '#ffffff');
        document.documentElement.style.setProperty('--aswg-bg-primary-bg-color', '#ffffff');
        document.documentElement.style.setProperty('--aswg-bg-primary-font-color', '#000000');
        document.documentElement.style.setProperty('--aswg-input-bg-color', 'whitesmoke');
        break;
      case 'dark':
        document.documentElement.style.setProperty('--aswg-primary-text-color', '#adbac7');
        document.documentElement.style.setProperty('--aswg-primary-color', '#46954a');
        document.documentElement.style.setProperty('--aswg-primary-color-hover', '#3b803e');
        document.documentElement.style.setProperty('--aswg-bg-primary-bg-color', '#22272e');
        document.documentElement.style.setProperty('--aswg-bg-primary-font-color', '#adbac7');
        document.documentElement.style.setProperty('--aswg-input-bg-color', '#2d333b');
        break;
    }
  }

  private saveTheme(theme: string) {
    localStorage.setItem('theme', theme);
  }

}
