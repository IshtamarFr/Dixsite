import { Component, OnInit } from '@angular/core';
import { MenuComponent } from '../../../../components/menu/menu.component';
import { ActivatedRoute } from '@angular/router';
import { PicvidService } from '../../services/picvid.service';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';
import { FileHandle } from '../../../../interfaces/file-handle.interface';
import { MatButtonModule } from '@angular/material/button';
import { DragMultipleDirective } from '../../../../directives/drag-multiple.directive';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-form-multiple-picvids',
  standalone: true,
  imports: [
    MenuComponent,
    MatIconModule,
    MatCardModule,
    CommonModule,
    MatButtonModule,
    DragMultipleDirective,
  ],
  templateUrl: './form-multiple-picvids.component.html',
  styleUrl: './form-multiple-picvids.component.scss',
})
export class FormMultiplePicvidsComponent implements OnInit {
  public albumId: number = 0;
  public fileHandle: FileHandle[] = [];
  public onError = false;
  public hideLoading: boolean = true;

  public constructor(
    private picvidService: PicvidService,
    private activatedRoute: ActivatedRoute,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.albumId = this.activatedRoute.snapshot.params['albumId'];
  }

  public back() {
    window.history.back();
  }

  filesDropped(files: FileHandle[]): void {
    this.fileHandle = files;
  }

  onFileSelected(event: any): void {
    if (event.target.files.length > 0) {
      for (let file of event.target.files) {
        const fileHandle: FileHandle = {
          file: file,
          url: this.sanitizer.bypassSecurityTrustUrl(
            window.URL.createObjectURL(file)
          ),
        };
        this.fileHandle.push(fileHandle);
      }
    }
  }

  sendAllPicvids(): void {
    this.onError = false;
    this.hideLoading = false;
    const formData = new FormData();

    for (let candidate of this.fileHandle) {
      formData.append('picvid', candidate.file, candidate.file.name);
    }

    this.picvidService.createMultiplePicvids(this.albumId, formData).subscribe({
      next: () => {
        this.hideLoading = true;
        this.back();
      },
      error: () => {
        this.onError = true;
        this.hideLoading = true;
      },
    });
  }

  removeCandidate(candidate: FileHandle): void {
    const index = this.fileHandle.indexOf(candidate);
    if (index !== -1) {
      this.fileHandle.splice(index, 1);
    }
  }
}
