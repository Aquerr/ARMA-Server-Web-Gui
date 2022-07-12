import { Component } from '@angular/core';
import {AuthService} from "./service/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'arma-web-gui';

  constructor(private authService: AuthService) {

  }

  isAuthenticated() {
    return this.authService.isAuthenticated();
  }
}
