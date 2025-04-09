import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../service/auth.service";
import {Router} from "@angular/router";
import {MaskService} from "../service/mask.service";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    standalone: false
})
export class LoginComponent implements OnInit {

  form: FormGroup;

  errorMessage?: string;

  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private maskService: MaskService,
              private router: Router) {
    this.form = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  login() {
    this.maskService.show();
    const formData = this.form.value;
    if (formData.username && formData.password) {
      this.authService.authenticate(formData.username, formData.password)
        .subscribe({
          next: () => {
            this.router.navigateByUrl('/general');
            this.maskService.hide();
          },
          error: err => {
            if(err.status === 400) {
              this.errorMessage = "Bad username or password!";
            } else {
              this.errorMessage = "Could not log in because of server error."
            }
            this.maskService.hide();
          }
        });
    }
  }

  onKeyDown(event: KeyboardEvent) {
    if (event.code === 'Enter' || event.code === 'NumpadEnter') {
      this.login();
    }
  }
}
