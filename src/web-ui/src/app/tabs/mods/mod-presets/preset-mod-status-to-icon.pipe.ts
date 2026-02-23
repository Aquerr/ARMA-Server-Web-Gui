import { Pipe, PipeTransform } from "@angular/core";
import { ModStatus } from "../../../model/mod.model";

@Pipe({
  name: "presetModStatusToIcon"
})
export class PresetModStatusToIconPipe implements PipeTransform {
  transform(status: ModStatus) {
    if (status === ModStatus.READY) {
      return "check_circle";
    } else if (status === ModStatus.INSTALLING) {
      return "settings";
    }
    return "download_for_offline";
  }
}
