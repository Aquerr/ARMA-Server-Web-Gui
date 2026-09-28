import { enableProdMode, importProvidersFrom, provideZonelessChangeDetection } from "@angular/core";
import { environment } from "@environments/environment";
import { bootstrapApplication } from "@angular/platform-browser";
import { AppComponent } from "@app/app.component";
import { provideHttpClient, withInterceptors, withXhr } from "@angular/common/http";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";
import { AppRoutingModule } from "@app/app-routing.module";
import { provideHotToastConfig } from "@ngxpert/hot-toast";
import { authInterceptor } from "@app/interceptors/auth.interceptor";
import { apiErrorInterceptor } from "@app/interceptors/api-error.interceptor";

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    provideZonelessChangeDetection(),
    importProvidersFrom(AppRoutingModule),
    provideHttpClient(withXhr(), withInterceptors([
      authInterceptor,
      apiErrorInterceptor
    ])),
    LoadingSpinnerMaskService,
    provideHotToastConfig({
      position: "top-right"
    })
  ]
}).catch((err) => console.error(err));
