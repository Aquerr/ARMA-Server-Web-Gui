import { ChangeDetectionStrategy, Component, computed, inject, input } from "@angular/core";
import { LoadingSpinnerMaskService } from "../service/loading-spinner-mask.service";

@Component({
  selector: "app-aswg-spinner",
  templateUrl: "./aswg-spinner.component.html",
  styleUrls: ["./aswg-spinner.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AswgSpinnerComponent {
  private loadingSpinnerService = inject(LoadingSpinnerMaskService);
  public fullScreen = input(false);
  public isVisible = computed(() => this.loadingSpinnerService.spinnerVisible());
}
