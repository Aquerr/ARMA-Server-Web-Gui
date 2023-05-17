import {Component, Input} from '@angular/core';
import {WorkshopMod} from '../../../model/workshop.model';

@Component({
  selector: 'app-workshop-item',
  templateUrl: './workshop-item.component.html',
  styleUrls: ['./workshop-item.component.css']
})
export class WorkshopItemComponent {
  @Input() workshopMod: WorkshopMod | undefined = undefined;
  @Input() canInstall: boolean = false;

  prepareModDescription(description: string | undefined) {
    let result = description;
    if (description) {
      result = this.removeFormattingCharacters(description);
      result = this.shorten(result, 400);

      if(description.length > result.length) {
        result += "...";
      }
    }

    return result;
  }

  private removeFormattingCharacters(text: string): string {
    const regex = /\[h1\]|\[\/h1\]|\[h2\]|\[\/h2\]|\[b\]|\[\/b\]|\[u\]|\[\/u\]|\[i\]|\[\/i\]|\[url.*\]|\[\/url\]|\[list\]|\[\/list\]|\[olist\]|\[\/olist\]|\[code\]|\[\/code\\]|\[table\]|\[\/table\]|\[\*\]/g;
    return text.replace(regex, "");
  }

  private shorten(text: string, maxLen: number, separator = ' ') {
    if (text.length <= maxLen) return text;
    return text.substring(0, text.lastIndexOf(separator, maxLen));
  }

  protected readonly undefined = undefined;
}
