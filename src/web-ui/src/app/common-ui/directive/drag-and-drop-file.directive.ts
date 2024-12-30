import {Directive, ElementRef, EventEmitter, HostListener, Output} from '@angular/core';

@Directive({
    selector: '[appDragAndDropFile]',
    standalone: false
})
export class DragAndDropFileDirective {

  @Output() fileDropped: EventEmitter<File> = new EventEmitter<File>();

  constructor(private hostElement: ElementRef) { }

  @HostListener('dragover', ['$event']) onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

  @HostListener('dragleave', ['$event']) onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

  @HostListener('drop', ['$event']) onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();

    const files = event.dataTransfer?.files;
    if (files) {
      if (files.length > 0) {
        for (let i = 0; i < files.length; i++) {
          this.fileDropped.emit(files[i]);
        }
      }
    }
  }
}
