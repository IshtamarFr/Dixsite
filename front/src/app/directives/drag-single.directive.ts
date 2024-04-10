import {
  Directive,
  EventEmitter,
  HostBinding,
  HostListener,
  Output,
} from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { FileHandle } from '../interfaces/file-handle.interface';

@Directive({
  selector: '[appDragSingle]',
  standalone: true,
})
export class DragSingleDirective {
  @Output() files: EventEmitter<FileHandle> = new EventEmitter();

  @HostBinding('style.background') private background = '#eee';

  constructor(private sanitizer: DomSanitizer) {}

  @HostListener('dragover', ['$event']) public onDragOver(evt: DragEvent) {
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#999';
  }

  @HostListener('dragleave', ['$event']) public onDragLeave(evt: DragEvent) {
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#eee';
  }

  @HostListener('drop', ['$event']) public onDrop(evt: DragEvent) {
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#eee';

    if (evt.dataTransfer) {
      const file = evt.dataTransfer.files[0];
      const url = this.sanitizer.bypassSecurityTrustUrl(
        window.URL.createObjectURL(file)
      );
      this.files.emit({ file, url } as FileHandle);
    }
  }
}
