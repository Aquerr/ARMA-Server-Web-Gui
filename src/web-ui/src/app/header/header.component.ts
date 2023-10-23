import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  @Input()
  darkMode: boolean = true;

  @Input()
  isMobile: boolean = false;

  @Output()
  changeThemeEmit: EventEmitter<void> = new EventEmitter();

  sideMenuExpanded = false;

  constructor() { }

  changeTheme() {
    this.changeThemeEmit.emit();
  }

  toggleSideMenu() {
    this.sideMenuExpanded = !this.sideMenuExpanded;
  }

  closeSideMenu() {
    this.sideMenuExpanded = false;
  }

}
