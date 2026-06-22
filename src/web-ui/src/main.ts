import { enableProdMode, importProvidersFrom, provideZonelessChangeDetection } from "@angular/core";
import { environment } from "./environments/environment";
import { bootstrapApplication } from "@angular/platform-browser";
import { AppComponent } from "./app/app.component";
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi, withXhr } from "@angular/common/http";
import { AswgHttpInterceptor } from "./app/interceptors/aswg-http.interceptor";
import { LoadingSpinnerMaskService } from "./app/service/loading-spinner-mask.service";
import { AppRoutingModule } from "./app/app-routing.module";
import { provideHotToastConfig } from "@ngxpert/hot-toast";

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    provideZonelessChangeDetection(),
    importProvidersFrom(AppRoutingModule),
    { provide: HTTP_INTERCEPTORS, useClass: AswgHttpInterceptor, multi: true },
    provideHttpClient(withXhr(), withInterceptorsFromDi()),
    LoadingSpinnerMaskService,
    provideHotToastConfig({
      position: "top-right"
    })
  ]
}).catch((err) => console.error(err));
