import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from "@angular/core";
import { CdlcService } from "../../service/cdlc.service";
import { Cdlc } from "../../model/cdlc.model";
import { NotificationService } from "src/app/service/notification.service";
import { LoadingSpinnerMaskService } from "../../service/loading-spinner-mask.service";
import { MatButton } from "@angular/material/button";
import { MatTooltip } from "@angular/material/tooltip";

@Component({
  selector: "app-cdlc",
  imports: [MatButton, MatTooltip],
  templateUrl: "./cdlc.component.html",
  styleUrl: "./cdlc.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CdlcComponent implements OnInit {
  private readonly cdlcService: CdlcService = inject(CdlcService);
  private readonly maskService: LoadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);

  cdlcList = signal<Cdlc[]>([]);

  ngOnInit(): void {
    this.reloadCdlcs();
  }

  private reloadCdlcs(): void {
    this.maskService.show();
    this.cdlcService.getAllCdlcs().subscribe((response) => {
      this.cdlcList.set(response.cdlcs);
      this.maskService.hide();
    });
  }

  toggleCdlc(cdlc: Cdlc) {
    this.maskService.show();
    this.cdlcService.toggleCdlc(cdlc.id).subscribe(() => {
      this.reloadCdlcs();
      this.notificationService.successNotification(
        `CDLC has been ${cdlc.enabled ? "disabled" : "enabled"}!`
      );
    });
  }
}
