import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  @Input()
  darkMode: boolean = true;

  @Output()
  changeThemeEmit: EventEmitter<void> = new EventEmitter();

  constructor() { }

  changeTheme() {
    this.changeThemeEmit.emit();
  }

}
