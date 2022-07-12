import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../service/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  form: FormGroup;

  errorMessage?: string;

  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private router: Router) {
    this.form = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  login() {
    const formData = this.form.value;

    if (formData.username && formData.password) {
      this.authService.authenticate(formData.username, formData.password)
        .subscribe(
          response => {
            console.log(response);
            console.log("User is logged in");
            this.router.navigateByUrl('/');
          },
          error => {
            console.log(error);
            if(error.status === 401) {
              this.errorMessage = "Bad username or password!";
            } else {
              this.errorMessage = "Could not log in because of server error."
            }
          }
        );
    }
  }

}
