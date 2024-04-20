import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Album } from '../../interfaces/album.interface';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { take } from 'rxjs';
import { AlbumService } from '../../../album/services/album.service';
import { AppSettings } from '../../../../utils/app-settings';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { DialogService } from '../../../../utils/dialog.service';

@Component({
  selector: 'app-main-album-card',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatSlideToggleModule,
    FormsModule,
    MatTooltipModule,
  ],
  templateUrl: './main-album-card.component.html',
  styleUrl: './main-album-card.component.scss',
})
export class MainAlbumCardComponent implements OnInit {
  @Input() album!: Album;
  @Input() userId!: number;
  @Input() context!: string;
  @Output() unsubscribeEmitter = new EventEmitter<number>();
  CONTACT_EMAIL = AppSettings.CONTACT_EMAIL;

  dateFormat: string = 'dd/MM/yyyy HH:mm';

  public constructor(
    private router: Router,
    private albumService: AlbumService,
    private dialogService: DialogService
  ) {}

  ngOnInit(): void {
    if (window.location.href.includes('/en/'))
      this.dateFormat = 'MM/dd/yyyy h:mm a';
  }

  getImageURL(imageId: string): string {
    return this.albumService.getImageURL(imageId);
  }

  onOwnToggleChange(album: Album): void {
    let newStatus: string;
    album.status === 'ONLINE'
      ? (newStatus = 'OFFLINE')
      : (newStatus = 'ONLINE');

    this.albumService
      .changeStatus(this.userId, album.id, newStatus)
      .pipe(take(1))
      .subscribe({
        next: () => {
          album.status = newStatus;
        },
      });
  }

  onUnsubscribe(album: Album): void {
    this.albumService
      .unSubscribeToAlbum(this.userId, album.id)
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.unsubscribeEmitter.emit(album.id);
        },
      });
  }

  openAlbum(album: Album): void {
    this.router.navigate(['/album', album.id]);
  }

  onUnmoderate(album: Album): void {
    this.dialogService
      .openConfirmDialog(
        $localize`Etes-vous sûr de vouloir cesser de modérer cet album ?`,
        true
      )
      .pipe(take(1))
      .subscribe({
        next: (confirmed) => {
          if (confirmed) {
            this.albumService
              .removeModo(album.owner_id, album.id, this.userId)
              .pipe(take(1))
              .subscribe({
                next: () => {
                  this.unsubscribeEmitter.emit(album.id);
                },
              });
          }
        },
      });
  }
}
