import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-mobile-header',
  templateUrl: './mobile-header.component.html',
  styleUrls: ['./mobile-header.component.css']
})
export class MobileHeaderComponent {

  @Input()
  darkMode: boolean = true;

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
