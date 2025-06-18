import {
  Directive,
  EventEmitter,
  HostListener, Input,
  Output, TemplateRef
} from "@angular/core";

@Directive({
  selector: "[appDragAndDropFile]",
  standalone: true
})
export class DragAndDropFileDirective {

  @Input() dropZoneElement!: TemplateRef<any>;

  @Output() fileDragged = new EventEmitter<boolean>();
  @Output() fileDropped: EventEmitter<File> = new EventEmitter<File>();

  constructor() {}

  @HostListener("dragover", ["$event"])
  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

  @HostListener("dragenter", ["$event"])
  onDragEnter(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();

    if (event.dataTransfer?.items
      && event.dataTransfer?.items.length > 0
      && event.dataTransfer?.items[0].kind === "file") {
      this.fileDragged.emit(true);
    }
  }

  @HostListener("dragleave", ["$event"])
  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();

    if (event.target === this.dropZoneElement.elementRef.nativeElement) {
      return;
    }

    this.fileDragged.emit(false);
  }

  @HostListener("drop", ["$event"])
  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.fileDragged.emit(false);

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
