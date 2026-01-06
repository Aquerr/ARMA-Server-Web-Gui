import { Component } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { AuthService } from "../service/auth.service";
import { Router } from "@angular/router";
import { LoadingSpinnerMaskService } from "../service/loading-spinner-mask.service";
import { HttpErrorResponse } from "@angular/common/http";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"],
  standalone: false
})
export class LoginComponent {
  form: FormGroup<{ username: FormControl<string>; password: FormControl<string> }>;

  errorMessage?: string;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private maskService: LoadingSpinnerMaskService,
    private router: Router
  ) {
    this.form = this.formBuilder.nonNullable.group({
      username: ["", Validators.required],
      password: ["", Validators.required]
    });
  }

  login() {
    this.maskService.show();
    const formData = this.form.value;
    if (formData.username && formData.password) {
      this.authService.authenticate(formData.username, formData.password).subscribe({
        next: () => {
          void this.router.navigateByUrl("/general");
          this.maskService.hide();
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 400) {
            this.errorMessage = "Bad username or password!";
          } else {
            this.errorMessage = "Could not log in because of server error.";
          }
          this.maskService.hide();
        }
      });
    }
  }

  onKeyDown(event: KeyboardEvent) {
    if (event.code === "Enter" || event.code === "NumpadEnter") {
      this.login();
    }
  }
}
