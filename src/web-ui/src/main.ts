import { enableProdMode, importProvidersFrom, provideZonelessChangeDetection } from "@angular/core";
import { environment } from "./environments/environment";
import { bootstrapApplication } from "@angular/platform-browser";
import { AppComponent } from "./app/app.component";
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { AswgHttpInterceptor } from "./app/interceptors/aswg-http.interceptor";
import { LoadingSpinnerMaskService } from "./app/service/loading-spinner-mask.service";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AppRoutingModule } from "./app/app-routing.module";
import { provideToastr } from "ngx-toastr";

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    provideZonelessChangeDetection(),
    provideToastr(),
    importProvidersFrom(BrowserAnimationsModule, AppRoutingModule),
    { provide: HTTP_INTERCEPTORS, useClass: AswgHttpInterceptor, multi: true },
    provideHttpClient(withInterceptorsFromDi()),
    LoadingSpinnerMaskService
  ]
}).catch((err) => console.error(err));
