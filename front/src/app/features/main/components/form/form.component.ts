import { Component, NgZone, OnInit } from '@angular/core';
import { MenuComponent } from '../../../../components/menu/menu.component';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AlbumService } from '../../../album/services/album.service';
import { SessionService } from '../../../../services/session.service';
import { NgIf } from '@angular/common';
import { FileHandle } from '../../../../interfaces/file-handle.interface';
import { DomSanitizer } from '@angular/platform-browser';
import { DragSingleDirective } from '../../../../directives/drag-single.directive';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [
    RouterLink,
    MenuComponent,
    MatCardModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    DragSingleDirective,
    NgIf,
  ],
  templateUrl: './form.component.html',
  styleUrl: './form.component.scss',
})
export class FormComponent implements OnInit {
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private ngZone: NgZone,
    private albumService: AlbumService,
    private sessionService: SessionService,
    private sanitizer: DomSanitizer
  ) {}

  public id: number = 0;
  public onError = false;
  public form!: FormGroup;
  public fileHandle: FileHandle | undefined;

  ngOnInit(): void {
    if (this.sessionService.user) this.id = this.sessionService.user.id;
    this.form = this.fb.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(63),
        ],
      ],
      description: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(255),
        ],
      ],
    });
  }

  public submit(): void {
    const formData = new FormData();
    formData.append('name', this.form!.get('name')!.value);
    formData.append('description', this.form!.get('description')!.value);
    if (this.fileHandle) {
      formData.append(
        'homePicture',
        this.fileHandle.file,
        this.fileHandle.file.name
      );
    }

    this.albumService.create(this.id, formData).subscribe({
      next: () => {
        this.ngZone.run(() => {
          this.router.navigate(['main']);
        });
      },
      error: () => (this.onError = true),
    });
  }

  public back() {
    window.history.back();
  }

  onFileSelected(event: any): void {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];

      const fileHandle: FileHandle = {
        file: file,
        url: this.sanitizer.bypassSecurityTrustUrl(
          window.URL.createObjectURL(file)
        ),
      };

      this.fileHandle = fileHandle;
    } else {
      this.fileHandle = undefined;
    }
  }

  filesDropped(file: FileHandle): void {
    this.fileHandle = file;
  }

  removeCandidate(): void {
    this.fileHandle = undefined;
  }
}
