import { Component, OnInit } from '@angular/core';
import { MenuComponent } from '../../../../components/menu/menu.component';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormsModule,
  FormGroup,
  FormBuilder,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FileHandle } from '../../../../interfaces/file-handle.interface';
import { DomSanitizer } from '@angular/platform-browser';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { PicvidService } from '../../services/picvid.service';
import { DragSingleDirective } from '../../../../directives/drag-single.directive';

@Component({
  selector: 'app-form-picvid',
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
    MatDatepickerModule,
    MatNativeDateModule,
    CommonModule,
    DragSingleDirective,
  ],
  providers: [MatDatepickerModule],
  templateUrl: './form-picvid.component.html',
  styleUrl: './form-picvid.component.scss',
})
export class FormPicvidComponent implements OnInit {
  public onError = false;
  public form!: FormGroup;
  public fileHandle: FileHandle | undefined;
  public albumId: number = 0;

  public constructor(
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private picvidService: PicvidService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.albumId = this.activatedRoute.snapshot.params['albumId'];

    this.form = this.fb.group({
      name: ['', [Validators.minLength(3), Validators.maxLength(63)]],
      description: ['', [Validators.minLength(3), Validators.maxLength(255)]],
      takenLocation: ['', [Validators.minLength(3), Validators.maxLength(63)]],
      date: ['', [Validators.minLength(3), Validators.maxLength(255)]],
      picvid: [''],
    });
  }

  public back() {
    window.history.back();
  }

  filesDropped(file: FileHandle): void {
    this.fileHandle = file;
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

  removeCandidate(): void {
    this.fileHandle = undefined;
  }

  submit(): void {
    const formData = new FormData();
    formData.append(
      'picvid',
      this.fileHandle!.file,
      this.fileHandle!.file.name
    );
    if (this.form.get('name') && this.form.get('name')?.value != '')
      formData.append('name', this.form.get('name')!.value);
    if (
      this.form.get('description') &&
      this.form.get('description')?.value != ''
    )
      formData.append('description', this.form.get('description')!.value);
    if (
      this.form.get('takenLocation') &&
      this.form.get('takenLocation')?.value != ''
    )
      formData.append('takenLocation', this.form.get('takenLocation')!.value);
    if (this.form.get('date') && this.form.get('date')?.value != '')
      formData.append('date', this.form.get('date')!.value);

    this.picvidService.createSinglePicvid(this.albumId, formData).subscribe({
      next: () => {
        this.back();
      },
      error: () => {
        this.onError = true;
      },
    });
  }
}
