import { Component, Input, NgZone, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { Album } from '../../../main/interfaces/album.interface';
import { AlbumService } from '../../services/album.service';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterModule } from '@angular/router';
import { MatDividerModule } from '@angular/material/divider';
import { FileHandle } from '../../../../interfaces/file-handle.interface';
import { DomSanitizer } from '@angular/platform-browser';
import { DragSingleDirective } from '../../../../directives/drag-single.directive';
import {
  ReactiveFormsModule,
  FormsModule,
  FormGroup,
  Validators,
  FormBuilder,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { take } from 'rxjs';
import { DialogService } from '../../../../utils/dialog.service';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatExpansionModule } from '@angular/material/expansion';

@Component({
  selector: 'app-album-title',
  standalone: true,
  imports: [
    MatCardModule,
    CommonModule,
    MatButtonModule,
    MatIconModule,
    RouterModule,
    MatDividerModule,
    DragSingleDirective,
    ReactiveFormsModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
    MatExpansionModule,
  ],
  templateUrl: './album-title.component.html',
  styleUrl: './album-title.component.scss',
})
export class AlbumTitleComponent implements OnInit {
  @Input() album!: Album;
  @Input() isAdminTriggered: boolean = false;
  @Input() nbPicvids: number = 0;

  public fileHandle: FileHandle | undefined;
  public form!: FormGroup;
  public form2!: FormGroup;
  public isHomePictureEmpty: boolean = false;

  constructor(
    private albumService: AlbumService,
    private sanitizer: DomSanitizer,
    private fb: FormBuilder,
    private dialogService: DialogService,
    private router: Router,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: [
        this.album.name,
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(63),
        ],
      ],
      description: [
        this.album.description,
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(255),
        ],
      ],
      homePicture: [''],
    });

    this.form2 = this.fb.group({
      moderatorEmail: [
        '',
        [Validators.required, Validators.maxLength(63), Validators.email],
      ],
    });
  }

  getImageURL(imageId: string): string {
    return this.albumService.getImageURL(imageId);
  }

  deleteAlbum(): void {
    this.dialogService
      .openInputDialog(
        "Tapez 'Supprimer' pour effacer définitivement cet album"
      )
      .subscribe({
        next: (resp) => {
          if (resp == 'Supprimer') {
            this.albumService
              .delete(this.album.id)
              .pipe(take(1))
              .subscribe({
                next: (_) => {
                  this.ngZone.run(() => {
                    this.router.navigate(['main']);
                  });
                },
              });
          }
        },
      });
  }

  deletePic(): void {
    this.isHomePictureEmpty = true;
    this.album.homePicture = null;
  }

  deleteTempPic(): void {
    this.fileHandle = undefined;
  }

  modifyAlbum(): void {
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
    if (this.isHomePictureEmpty) {
      formData.append('isHomePictureEmpty', '' + this.isHomePictureEmpty);
    }

    this.albumService
      .modify(this.album.id, formData)
      .pipe(take(1))
      .subscribe({
        next: (album) => {
          this.album = album;
        },
      });
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

  addModo(): void {
    this.albumService
      .addModo(
        this.album.owner_id,
        this.album.id,
        this.form2.get('moderatorEmail')!.value
      )
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          this.album.moderator_ids = resp.moderator_ids;
          this.album.moderator_names = resp.moderator_names;
        },
      });
    this.form2.get('moderatorEmail')!.reset();
  }
}
